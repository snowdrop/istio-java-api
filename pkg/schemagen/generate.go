/**
 * Copyright (C) 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemagen

import (
	"errors"
	"fmt"
	"path/filepath"
	"reflect"
	"sort"
	"strings"
	"time"
)

type PackageDescriptor struct {
	GoPackage   string
	JavaPackage string
	Prefix      string
	Visited     bool
}

type CrdDescriptor struct {
	Name    string
	CrdType string
	Visited bool
}

type schemaGenerator struct {
	types             map[reflect.Type]*JSONObjectDescriptor
	packages          map[string]PackageDescriptor
	enumMap           map[string]string
	interfacesMap     map[string]string
	interfacesImpls   map[string]string
	unknownEnums      []string
	unknownInterfaces []string
	crds              map[string]CrdDescriptor
}

func (g *schemaGenerator) getPackage(name string) (PackageDescriptor, bool) {
	descriptor, ok := g.packages[name]
	if ok {
		descriptor.Visited = true
		g.packages[name] = descriptor
	}

	return descriptor, ok
}

func GenerateSchema(t reflect.Type, packages []PackageDescriptor, enumMap map[string]string, interfacesMap map[string]string, interfacesImpl map[string]string, crds map[string]CrdDescriptor, strict bool) (*JSONSchema, error) {
	g := newSchemaGenerator(packages, enumMap, interfacesMap, interfacesImpl, crds)
	return g.generate(t, strict)
}

func newSchemaGenerator(packages []PackageDescriptor, enumMap map[string]string, interfacesMap map[string]string, interfacesImpl map[string]string, crds map[string]CrdDescriptor) *schemaGenerator {
	pkgMap := make(map[string]PackageDescriptor)
	for _, p := range packages {
		pkgMap[p.GoPackage] = p
	}

	g := schemaGenerator{
		types:           make(map[reflect.Type]*JSONObjectDescriptor),
		packages:        pkgMap,
		enumMap:         enumMap,
		interfacesMap:   interfacesMap,
		interfacesImpls: interfacesImpl,
		crds:            crds,
	}
	return &g
}

func getFieldName(f reflect.StructField) string {
	field := getSubTag(f, "protobuf", "json")
	if len(field) > 0 {
		return field
	} else {
		// we don't always have a json field in protobuf so use name field when available
		field = getSubTag(f, "protobuf", "name")
		if len(field) > 0 {
			return field
		} else {
			json := f.Tag.Get("json")
			if len(json) > 0 {
				return json
			} else {
				return strings.ToLower(f.Name[:1]) + f.Name[1:]
			}
		}
	}
}

func getFieldEnum(f reflect.StructField) string {
	return getSubTag(f, "protobuf", "enum")
}

func getSubTag(f reflect.StructField, main string, sub string) string {
	tag := f.Tag.Get(main)
	if len(tag) > 0 {
		parts := strings.Split(tag, ",")
		for _, part := range parts {
			initial := part
			part = strings.TrimPrefix(part, sub+"=")
			if initial != part {
				return part
			}
		}
	}

	return ""
}

func getFieldDescription(f reflect.StructField) string {
	json := f.Tag.Get("description")
	if len(json) > 0 {
		parts := strings.Split(json, ",")
		return parts[0]
	}
	return ""
}

func (g *schemaGenerator) qualifiedName(t reflect.Type) string {
	path := pkgPath(t)
	pkgDesc, ok := g.getPackage(path)
	name := t.Name()
	if !ok {
		return escapedQualifiedName(path) + "_" + name
	} else {
		return pkgDesc.Prefix + name
	}
}

func escapedQualifiedName(path string) string {
	prefix := strings.Replace(path, "/", "_", -1)
	prefix = strings.Replace(prefix, ".", "_", -1)
	prefix = strings.Replace(prefix, "-", "_", -1)
	return prefix
}

func (g *schemaGenerator) generateReferenceFrom(typeName string) string {
	return "#/definitions/" + typeName
}

func (g *schemaGenerator) generateReference(t reflect.Type) string {
	return g.generateReferenceFrom(g.qualifiedName(t))
}

func (g *schemaGenerator) generateEnumTypeAccumulatingUnknown(t string, humanReadableFieldName string) (string, string) {
	enum, ok := g.enumMap[t]
	if !ok {
		g.unknownEnums = append(g.unknownEnums, humanReadableFieldName+". Add "+t+" to generate.generate.enumMap.")
	}

	return g.generateReferenceFrom(escapedQualifiedName(enum)), enum
}

func (g *schemaGenerator) javaTypeArrayList(t reflect.Type) string {
	typeName := g.javaTypeWrapPrimitive(t)
	switch typeName {
	case "Byte", "Integer":
		return "String"
	default:
		return "java.util.ArrayList<" + typeName + ">"
	}
}

func (g *schemaGenerator) javaTypeWrapPrimitive(t reflect.Type) string {
	typeName := g.javaType(t)
	switch typeName {
	case "bool":
		return "Boolean"
	case "char":
		return "Character"
	case "short":
		return "Short"
	case "int":
		return "Integer"
	case "long":
		return "Long"
	case "float":
		return "Float"
	case "double":
		return "Double"
	default:
		return typeName
	}
}

func (g *schemaGenerator) javaType(t reflect.Type) string {
	if t.Kind() == reflect.Ptr {
		t = t.Elem()
	}
	name := t.Name()

	// deal with "inner" structs
	underscore := strings.LastIndex(name, "_")
	if underscore >= 0 {
		// check if we have an interface which we should rename
		interfaceName := getQualifiedInterfaceName(t)
		interfaceFQN, ok := g.interfacesImpls[interfaceName]
		if ok {
			dot := strings.LastIndex(interfaceFQN, ".")
			interfaceName := interfaceFQN[dot+1:]
			name = name[underscore+1:] + interfaceName
		} else {
			name = name[underscore+1:]
		}
	}

	path := pkgPath(t)

	if strings.Contains(path, "istio.io") {
		// transform type name if needed, checking if the type is a top-level one and thus a CRD
		var isCRD = true
		if strings.Contains(path, "template") {
			name, isCRD = transformTemplateName(name, path)
		} else if strings.Contains(path, "adapter") {
			name, isCRD = transformAdapterName(name, path)
		}

		// if the type name is still marked as CRD, add Spec suffix to its name if it's a known CRD name
		// we need this "double" check because some adapter/template classes have the same name as top-level CRDs (e.g. Quota)
		if isCRD {
			// attempt to retrieve the version from the path
			version := filepath.Base(path)
			if strings.HasPrefix(version, "v") {
				lower := version + "." + strings.ToLower(name)
				crdDesc, ok := g.crds[lower]
				if ok {
					name += "Spec"
					g.crds[lower] = CrdDescriptor{
						Name:    crdDesc.Name,
						CrdType: crdDesc.CrdType,
						Visited: true,
					}
				}
			}
		}
	}

	pkgDesc, ok := g.getPackage(path)
	if ok && (t.Kind() == reflect.Struct || t.Kind() == reflect.Interface) {
		switch name {
		case "Time":
			return "String"
		case "RawExtension":
			return "io.fabric8.kubernetes.api.model.HasMetadata"
		case "List":
			return pkgDesc.JavaPackage + ".BaseKubernetesList"
		case "Duration":
			return "me.snowdrop.istio.api.Duration"
		case "TimeStamp":
			return "me.snowdrop.istio.api.TimeStamp"
		case "Timestamp":
			return "me.snowdrop.istio.api.TimeStamp"
		case "Value":
			if strings.Contains(pkgDesc.GoPackage, "protobuf") {
				return pkgDesc.JavaPackage + "." + name
			}
			return "me.snowdrop.istio.api.cexl.TypedValue"
		case "AttributeValue":
			return "me.snowdrop.istio.api.cexl.TypedValue"
		default:
			return pkgDesc.JavaPackage + "." + name
		}
	} else {
		switch t.Kind() {
		case reflect.Bool:
			return "bool"
		case reflect.Int, reflect.Int8, reflect.Int16,
			reflect.Int32, reflect.Uint,
			reflect.Uint8, reflect.Uint16, reflect.Uint32:
			return "int"
		case reflect.Int64, reflect.Uint64:
			return "Long"
		case reflect.Float32, reflect.Float64, reflect.Complex64,
			reflect.Complex128:
			return "double"
		case reflect.String:
			return "String"
		case reflect.Array, reflect.Slice:
			return g.javaTypeArrayList(t.Elem())
		case reflect.Map:
			return "java.util.Map<String," + g.javaTypeWrapPrimitive(t.Elem()) + ">"
		default:
			switch name {
			case "Time":
				return "String" // todo: fix me?
			default:
				if len(name) == 0 && t.NumField() == 0 {
					return "Object"
				} else {
					return name
				}
			}
		}
	}
}

func transformTemplateName(original string, path string) (string, bool) {
	kind := "template"
	var name = original
	result := false
	kindIndex := strings.Index(path, kind)
	if kindIndex >= 0 && strings.Compare("InstanceMsg", name) == 0 {
		// extract specific type name from path
		extractedTypeName := path[kindIndex+len(kind)+1:]
		name = strings.Title(extractedTypeName)
		result = true
	}

	// same replacements should occur in IstioSpecRegistry.getCRDInfoFrom method
	if strings.Contains(name, "entry") {
		return strings.Replace(name, "entry", "Entry", -1), result
	} else if strings.Contains(name, "Msg") {
		return strings.Replace(name, "Msg", "", -1), result
	} else if strings.Contains(name, "nothing") {
		return strings.Replace(name, "nothing", "Nothing", -1), result
	} else if strings.Contains(name, "key") {
		return strings.Replace(name, "key", "Key", -1), result
	} else if strings.Contains(name, "span") {
		return strings.Replace(name, "span", "Span", -1), result
	} else {
		return name, result
	}
}

func transformAdapterName(original string, path string) (string, bool) {
	kind := "adapter"
	var name = original
	result := false
	kindIndex := strings.Index(path, kind)
	if kindIndex >= 0 && strings.Compare("Params", name) == 0 {
		// extract specific type name from path
		extractedTypeName := path[kindIndex+len(kind)+1:]
		slashIndex := strings.IndexRune(extractedTypeName, '/')
		if slashIndex >= 0 {
			extractedTypeName = extractedTypeName[:slashIndex]
		}
		name = strings.Title(extractedTypeName)
		result = true
	}

	underscore := strings.IndexRune(name, '_')
	if underscore > 0 {
		name = name[underscore+1:]
	}

	return name, result
}

func (g *schemaGenerator) generate(t reflect.Type, strict bool) (*JSONSchema, error) {
	if t.Kind() != reflect.Struct {
		return nil, fmt.Errorf("only struct types can be converted")
	}

	s := JSONSchema{
		ID:     "http://snowdrop.me/istio/v1/" + t.Name() + "#",
		Schema: "http://json-schema.org/schema#",
		JSONDescriptor: JSONDescriptor{
			Type: "object",
		},
	}
	s.JSONObjectDescriptor = g.generateObjectDescriptor(t)
	if len(g.types) > 0 {
		s.Definitions = make(map[string]JSONPropertyDescriptor)

		for k, v := range g.types {
			name := g.qualifiedName(k)
			descriptor := &JSONDescriptor{
				Type: "object",
			}

			interfaceName := getQualifiedInterfaceName(k)
			i, ok := g.interfacesImpls[interfaceName]
			if ok {
				descriptor.JavaInterfaces = []string{i}
			}

			value := JSONPropertyDescriptor{
				JSONDescriptor:       descriptor,
				JSONObjectDescriptor: v,
				JavaTypeDescriptor: &JavaTypeDescriptor{
					JavaType: g.javaType(k),
				},
			}
			s.Definitions[name] = value
		}
	}

	if strict {
		// check if there are API packages that weren't visited, which would indicate classes that were missed
		unvisitedPkgs := make([]string, 0)
		for _, pkgDesc := range g.packages {
			if !pkgDesc.Visited && strings.HasPrefix(pkgDesc.GoPackage, "istio.io/api") {
				unvisitedPkgs = append(unvisitedPkgs, pkgDesc.GoPackage)
			}
		}
		unvisitedCRDs := make([]string, 0)
		for crd, crdDesc := range g.crds {
			if !crdDesc.Visited {
				unvisitedCRDs = append(unvisitedCRDs, crd+": "+crdDesc.CrdType)
			}
		}
		sort.Strings(unvisitedCRDs)
		hasUnvisitedCRDs := len(unvisitedCRDs) > 0
		hasUnvisitedPkgs := len(unvisitedPkgs) > 0
		hasUnknownEnums := len(g.unknownEnums) > 0
		hasUnknownInterfaces := len(g.unknownInterfaces) > 0
		if hasUnknownEnums || hasUnvisitedPkgs || hasUnknownInterfaces || hasUnvisitedCRDs {
			var msg string
			if hasUnknownEnums {
				msg = msg + "\n\nUnknown enums:\n" + strings.Join(g.unknownEnums, "\n")
			}
			if hasUnknownInterfaces {
				msg = msg + "\n\nUnknown interfaces:\n" + strings.Join(g.unknownInterfaces, "\n")
			}
			if hasUnvisitedPkgs {
				msg = msg + "\n\nUnvisited packages:\n" + strings.Join(unvisitedPkgs, "\n")
			}
			if hasUnvisitedCRDs {
				msg = msg + "\n\nUnvisited CRDs:\n" + strings.Join(unvisitedCRDs, "\n")
			}

			return &s, errors.New(msg)
		}
	}

	return &s, nil
}

// Compute a qualified name formatted as expected by interface maps to check for candidate interfaces
func getQualifiedInterfaceName(k reflect.Type) string {
	typeName := k.Name()
	path := pkgPath(k)

	// special case for # isValue_Kind field kind in github.com/gogo/protobuf/types/Value
	if strings.HasPrefix(path, "github.com/gogo/protobuf/types") {
		path = "api"
	} else {
		// first get the pkg path for the type and remove the istio.io prefix
		path = strings.TrimPrefix(path, "istio.io/")

		// if we're looking at an adapter or template type, we need to remove the "istio" prefix
		isAdapter := strings.Contains(path, "adapter")
		isTemplate := strings.Contains(path, "template")
		if isAdapter || isTemplate {
			path = strings.TrimPrefix(path, "istio/")

			// if we're dealing with an adapter, we also need to remove the trailing "config" package
			if isAdapter {
				path = strings.Replace(path, "/config", "", 1)
			}
		}
	}

	// finally we replace the path separators by '.' to match the Java package name as defined in generate/generate#loadInterfacesData
	path = strings.Replace(path, "/", ".", -1) + "." + typeName
	return path
}

var timeType = reflect.TypeOf(time.Time{})
var stringType = reflect.TypeOf("")

func (g *schemaGenerator) getPropertyDescriptor(t reflect.Type, desc string, humanReadableFieldName string) JSONPropertyDescriptor {
	if t.Kind() == reflect.Ptr {
		t = t.Elem()
	}

	// specific handling of time.Time properties converted as strings
	if t == timeType {
		t = stringType
	}

	switch t.Kind() {
	case reflect.Bool:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "boolean",
				Description: desc,
			},
		}
	case reflect.Int, reflect.Int8, reflect.Int16,
		reflect.Int32, reflect.Uint,
		reflect.Uint8, reflect.Uint16, reflect.Uint32:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "integer",
				Description: desc,
			},
		}
	case reflect.Int64, reflect.Uint64:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "integer",
				Description: desc,
			},
			JavaTypeDescriptor: &JavaTypeDescriptor{
				JavaType: "Long",
			},
		}
	case reflect.Float32, reflect.Float64, reflect.Complex64,
		reflect.Complex128:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "number",
				Description: desc,
			},
		}
	case reflect.String:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "string",
				Description: desc,
			},
		}
	case reflect.Array:
	case reflect.Slice:
		if g.javaTypeArrayList(t.Elem()) == "String" {
			return JSONPropertyDescriptor{
				JSONDescriptor: &JSONDescriptor{
					Type:        "string",
					Description: desc,
				},
			}
		} else {
			return JSONPropertyDescriptor{
				JSONDescriptor: &JSONDescriptor{
					Type:        "array",
					Description: desc,
				},
				JSONArrayDescriptor: &JSONArrayDescriptor{
					Items: g.getPropertyDescriptor(t.Elem(), desc, ""),
				},
			}
		}
	case reflect.Map:
		return JSONPropertyDescriptor{
			JSONDescriptor: &JSONDescriptor{
				Type:        "object",
				Description: desc,
			},
			JSONMapDescriptor: &JSONMapDescriptor{
				MapValueType: g.getPropertyDescriptor(t.Elem(), desc, ""),
			},
			JavaTypeDescriptor: &JavaTypeDescriptor{
				ExistingJavaType: "java.util.Map<String," + g.javaTypeWrapPrimitive(t.Elem()) + ">",
			},
		}
	case reflect.Struct:
		definedType, ok := g.types[t]
		if !ok {
			g.types[t] = &JSONObjectDescriptor{}
			definedType = g.generateObjectDescriptor(t)
			g.types[t] = definedType
		}
		return JSONPropertyDescriptor{
			JSONReferenceDescriptor: &JSONReferenceDescriptor{
				Reference: g.generateReference(t),
			},
			JavaTypeDescriptor: &JavaTypeDescriptor{
				JavaType: g.javaType(t),
			},
		}
	case reflect.Interface:
		name := getQualifiedInterfaceName(t)
		interfaceType, ok := g.interfacesMap[name]
		if !ok {
			// special cases for AttributeValue and Value which are handled by TypedValue
			switch name {
			case "api.mixer.v1.isAttributes_AttributeValue_Value":
			case "api.policy.v1beta1.isValue_Value":
			default:
				g.unknownInterfaces = append(g.unknownInterfaces, humanReadableFieldName)
			}
			interfaceType = g.javaType(t)
		}

		return JSONPropertyDescriptor{
			JavaTypeDescriptor: &JavaTypeDescriptor{
				ExistingJavaType: interfaceType,
				IsInterface:      true,
			},
		}
	}

	return JSONPropertyDescriptor{}
}

func (g *schemaGenerator) getStructProperties(t reflect.Type) map[string]JSONPropertyDescriptor {
	props := map[string]JSONPropertyDescriptor{}

	// specific handling for CorsPolicy.allowOrigin vs. allowOrigins
	const corsPolicyTypeName = "CorsPolicy"
	const allowOriginFieldName = "allowOrigin"
	const renamedAllowOriginFieldName = "deprecatedAllowOrigin"
	isCorsPolicy := t.Name() == corsPolicyTypeName

	for i := 0; i < t.NumField(); i++ {
		field := t.Field(i)
		if len(field.PkgPath) > 0 { // Skip private fields
			continue
		}
		name := getFieldName(field)
		// Skip unserialized fields
		if name == "-" {
			continue
		}

		// rename allowOrigin to deprecatedAllowOrigin on CorsPolicy
		if isCorsPolicy && name == allowOriginFieldName {
			name = renamedAllowOriginFieldName
		}

		path := pkgPath(t)
		humanReadableFieldName := field.Type.Name() + " field " + name + " in " + path + "/" + t.Name()

		desc := getFieldDescription(field)
		enum := getFieldEnum(field)
		var prop JSONPropertyDescriptor
		if len(enum) > 0 {
			_, javaType := g.generateEnumTypeAccumulatingUnknown(enum, humanReadableFieldName)
			prop = JSONPropertyDescriptor{
				JavaTypeDescriptor: &JavaTypeDescriptor{
					ExistingJavaType: javaType,
				},
			}
		} else {
			prop = g.getPropertyDescriptor(field.Type, desc, humanReadableFieldName)
		}
		if field.Anonymous && field.Type.Kind() == reflect.Struct && len(name) == 0 {
			var newProps map[string]JSONPropertyDescriptor
			if prop.JSONReferenceDescriptor != nil {
				pType := field.Type
				if pType.Kind() == reflect.Ptr {
					pType = pType.Elem()
				}
				newProps = g.types[pType].Properties
			} else {
				newProps = prop.Properties
			}
			for k, v := range newProps {
				switch k {
				case "kind":
					v = JSONPropertyDescriptor{
						JSONDescriptor: &JSONDescriptor{
							Type:     "string",
							Default:  t.Name(),
							Required: true,
						},
					}
				case "apiVersion":
					apiVersion := filepath.Base(path)
					apiGroup := filepath.Base(strings.TrimSuffix(path, apiVersion))
					if apiGroup != "api" {
						groupPostfix := ""
						if strings.HasPrefix(path, "github.com/openshift/origin/pkg/") {
							groupPostfix = ".openshift.io"
						}
						apiVersion = apiGroup + groupPostfix + "/" + apiVersion
					}
					v = JSONPropertyDescriptor{
						JSONDescriptor: &JSONDescriptor{
							Type: "string",
						},
					}
					if apiVersion != "unversioned" {
						v.Required = true
						v.Default = apiVersion
					}
				default:
					g.addConstraints(t.Name(), k, &v)
				}
				props[k] = v
			}
		} else {
			g.addConstraints(t.Name(), name, &prop)
			props[name] = prop
		}
	}
	return props
}

func (g *schemaGenerator) generateObjectDescriptor(t reflect.Type) *JSONObjectDescriptor {
	desc := JSONObjectDescriptor{AdditionalProperties: false}
	desc.Properties = g.getStructProperties(t)
	return &desc
}

func (g *schemaGenerator) addConstraints(objectName string, propName string, prop *JSONPropertyDescriptor) {
	// no constraints for now, keeping the method for when we need to add some
	/*switch objectName {
	case "ObjectMeta":
		switch propName {
		case "namespace":
			prop.Pattern = `^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$`
			prop.MaxLength = 253
		}
	case "EnvVar":
		switch propName {
		case "name":
			prop.Pattern = `^[A-Za-z_][A-Za-z0-9_]*$`
		}
	case "Container", "Volume", "ContainerPort", "ContainerStatus", "ServicePort", "EndpointPort":
		switch propName {
		case "name":
			prop.Pattern = `^[a-z0-9]([-a-z0-9]*[a-z0-9])?$`
			prop.MaxLength = 63
		}
	}*/
}

func pkgPath(t reflect.Type) string {
	path := t.PkgPath()
	return strings.TrimPrefix(path, "github.com/snowdrop/istio-java-api/vendor/")
}
