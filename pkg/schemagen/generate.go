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
	"strings"
)

type PackageDescriptor struct {
	GoPackage   string
	JavaPackage string
	Prefix      string
}

type schemaGenerator struct {
	types             map[reflect.Type]*JSONObjectDescriptor
	packages          map[string]PackageDescriptor
	enumMap           map[string]string
	interfacesMap     map[string]string
	interfacesimpl    map[string]string
	typeMap           map[reflect.Type]reflect.Type
	unknownEnums      []string
	unknownInterfaces []string
}

func GenerateSchema(t reflect.Type, packages []PackageDescriptor, typeMap map[reflect.Type]reflect.Type, enumMap map[string]string, interfacesMap map[string]string, interfacesImpl map[string]string) (*JSONSchema, error) {
	g := newSchemaGenerator(packages, typeMap, enumMap, interfacesMap, interfacesImpl)
	return g.generate(t)
}

func newSchemaGenerator(packages []PackageDescriptor, typeMap map[reflect.Type]reflect.Type, enumMap map[string]string, interfacesMap map[string]string, interfacesImpl map[string]string) *schemaGenerator {
	pkgMap := make(map[string]PackageDescriptor)
	for _, p := range packages {
		pkgMap[p.GoPackage] = p
	}
	g := schemaGenerator{
		types:          make(map[reflect.Type]*JSONObjectDescriptor),
		packages:       pkgMap,
		typeMap:        typeMap,
		enumMap:        enumMap,
		interfacesMap:  interfacesMap,
		interfacesimpl: interfacesImpl,
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
			return f.Name
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
	pkgDesc, ok := g.packages[path]
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

func (g *schemaGenerator) resourceDetails(t reflect.Type) string {
	var name = strings.ToLower(t.Name())
	return name
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
		g.unknownEnums = append(g.unknownEnums, humanReadableFieldName)
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
	var underscore = strings.IndexRune(name, '_')
	if underscore >= 0 {
		// check if we have an interface which we should rename
		_, ok := g.interfacesimpl[name]
		if ok {

			// handle Params_MetricInfo_BucketsDefinition_ExplicitBuckets cases
			if strings.Contains(name, "BucketsDefinition") {
				underscore = strings.LastIndex(name, "_")
				name = name[underscore+1:] + "Definition"
			} else {
				interfaceName := name[:underscore]
				implName := name[underscore+1:]
				// check if implementation name still has an _ in it as in: HTTPFaultInjection_Delay_ExponentialDelay
				underscore = strings.IndexRune(implName, '_')
				if underscore > 0 {
					// in that case, keep only the part after the underscore if it doesn't contain the part before
					intName := implName[:underscore]
					lastImplName := implName[underscore+1:]
					if strings.Contains(lastImplName, intName) {
						implName = lastImplName
					} else {
						implName = lastImplName + intName
					}
				}
				name = implName + interfaceName
			}

		} else {
			name = name[underscore+1:]
		}
	}

	path := pkgPath(t)

	// transform type name if needed
	if strings.Contains(path, "template") {
		name = transformTemplateName(name, path)
	} else if strings.Contains(path, "adapter") {
		name = transformAdapterName(name, path)
	}

	pkgDesc, ok := g.packages[path]
	if t.Kind() == reflect.Struct && ok {
		switch name {
		case "Time":
			return "String"
		case "RawExtension":
			return "io.fabric8.kubernetes.api.model.HasMetadata"
		case "List":
			return pkgDesc.JavaPackage + ".BaseKubernetesList"
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
			case "BoolValue":
				return "java.lang.Boolean"
			case "Duration":
				return "me.snowdrop.istio.api.model.Duration"
			case "TimeStamp":
				return "me.snowdrop.istio.api.model.TimeStamp"
			case "Timestamp":
				return "me.snowdrop.istio.api.model.TimeStamp"
			case "Value":
				return "me.snowdrop.istio.api.model.v1.cexl.TypedValue"
			case "StringMatch":
				return "me.snowdrop.istio.api.model.v1.networking.StringMatch"
			case "PortSelector":
				return "me.snowdrop.istio.api.model.v1.networking.PortSelector"
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

func transformTemplateName(original string, path string) string {
	kind := "template"
	var name = original
	kindIndex := strings.Index(path, kind)
	if kindIndex >= 0 && strings.Compare("InstanceMsg", name) == 0 {
		// extract specific type name from path
		extractedTypeName := path[kindIndex+len(kind)+1:]
		name = strings.Title(extractedTypeName)
	}

	// same replacements should occur in IstioSpecRegistry.getCRDInfoFrom method
	if strings.Contains(name, "entry") {
		return strings.Replace(name, "entry", "Entry", -1)
	} else if strings.Contains(name, "Msg") {
		return strings.Replace(name, "Msg", "", -1)
	} else if strings.Contains(name, "nothing") {
		return strings.Replace(name, "nothing", "Nothing", -1)
	} else if strings.Contains(name, "key") {
		return strings.Replace(name, "key", "Key", -1)
	} else if strings.Contains(name, "span") {
		return strings.Replace(name, "span", "Span", -1)
	} else {
		return name
	}
}

func transformAdapterName(original string, path string) string {
	kind := "adapter"
	var name = original
	kindIndex := strings.Index(path, kind)
	if kindIndex >= 0 && strings.Compare("Params", name) == 0 {
		// extract specific type name from path
		extractedTypeName := path[kindIndex+len(kind)+1:]
		slashIndex := strings.IndexRune(extractedTypeName, '/')
		if slashIndex >= 0 {
			extractedTypeName = extractedTypeName[:slashIndex]
		}
		name = strings.Title(extractedTypeName)
	}

	underscore := strings.IndexRune(name, '_')
	if underscore > 0 {
		name = name[underscore+1:]
	}

	return name
}

func (g *schemaGenerator) generate(t reflect.Type) (*JSONSchema, error) {
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
		s.Resources = make(map[string]*JSONObjectDescriptor)

		for k, v := range g.types {
			name := g.qualifiedName(k)
			resource := g.resourceDetails(k)
			descriptor := &JSONDescriptor{
				Type: "object",
			}

			typeName := k.Name()
			i, ok := g.interfacesimpl[typeName]
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
			s.Resources[resource] = v
		}
	}

	hasUnknownEnums := len(g.unknownEnums) > 0
	hasUnknownInterfaces := len(g.unknownInterfaces) > 0
	if hasUnknownEnums /*|| hasUnknownInterfaces*/ {
		var msg string
		if hasUnknownEnums {
			msg = msg + "\nUnknown enums:\n" + strings.Join(g.unknownEnums, "\n")
		}
		if hasUnknownInterfaces {
			msg = msg + "\nUnknown interfaces:\n" + strings.Join(g.unknownInterfaces, "\n")
		}

		return &s, errors.New(msg)
	} else {
		return &s, nil
	}
}

func (g *schemaGenerator) getPropertyDescriptor(t reflect.Type, desc string, humanReadableFieldName string) JSONPropertyDescriptor {
	if t.Kind() == reflect.Ptr {
		t = t.Elem()
	}
	tt, ok := g.typeMap[t]
	if ok {
		t = tt
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
				JavaType: "java.util.Map<String," + g.javaTypeWrapPrimitive(t.Elem()) + ">",
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
		name := t.Name()
		_, ok := g.interfacesMap[name]
		if !ok {
			g.unknownInterfaces = append(g.unknownInterfaces, humanReadableFieldName)
		}
	}

	return JSONPropertyDescriptor{}
}

func (g *schemaGenerator) getStructProperties(t reflect.Type) map[string]JSONPropertyDescriptor {
	props := map[string]JSONPropertyDescriptor{}
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

		// Skip dockerImageMetadata field
		path := pkgPath(t)
		if path == "github.com/openshift/origin/pkg/image/api/v1" && t.Name() == "Image" && name == "dockerImageMetadata" {
			continue
		}

		humanReadableFieldName := field.Type.Name() + " field " + name + " in " + path + "/" + t.Name()

		desc := getFieldDescription(field)
		enum := getFieldEnum(field)
		var prop JSONPropertyDescriptor
		if len(enum) > 0 {
			_, javaType := g.generateEnumTypeAccumulatingUnknown(enum, humanReadableFieldName)
			prop = JSONPropertyDescriptor{
				JavaTypeDescriptor: &JavaTypeDescriptor{
					JavaType: javaType,
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
	desc := JSONObjectDescriptor{AdditionalProperties: true}
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
