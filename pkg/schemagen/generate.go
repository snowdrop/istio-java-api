/**
 * Copyright (C) 2011 Red Hat, Inc.
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
	"fmt"
	"path/filepath"
	"reflect"
	"strings"
	"errors"
)

type PackageDescriptor struct {
	GoPackage   string
	JavaPackage string
	Prefix      string
}

type schemaGenerator struct {
	types        map[reflect.Type]*JSONObjectDescriptor
	packages     map[string]PackageDescriptor
	enumMap      map[string]string
	typeMap      map[reflect.Type]reflect.Type
	unknownEnums []string
}

func GenerateSchema(t reflect.Type, packages []PackageDescriptor, typeMap map[reflect.Type]reflect.Type, enumMap map[string]string) (*JSONSchema, error) {
	g := newSchemaGenerator(packages, typeMap, enumMap)
	return g.generate(t)
}

func newSchemaGenerator(packages []PackageDescriptor, typeMap map[reflect.Type]reflect.Type, enumMap map[string]string) *schemaGenerator {
	pkgMap := make(map[string]PackageDescriptor)
	for _, p := range packages {
		pkgMap[p.GoPackage] = p
	}
	g := schemaGenerator{
		types:    make(map[reflect.Type]*JSONObjectDescriptor),
		packages: pkgMap,
		typeMap:  typeMap,
		enumMap:  enumMap,
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

func (g *schemaGenerator) generateEnumTypeAccumulatingUnknown(t string) (string, string) {
	enum, ok := g.enumMap[t]
	if !ok {
		g.unknownEnums = append(g.unknownEnums, t)
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
	underscore := strings.IndexRune(name, '_')
	if underscore >= 0 {
		name = name[underscore+1:]
	}

	path := pkgPath(t)

	// transforms the type name if the path matches specific parts identifying a type kind and the name matches the specified typeName
	// this is used to process templates and adapters automatically which use a fixed entry point struct name
	nameTransformer := func(kind, typeName string) (string, bool) {
		kindIndex := strings.Index(path, kind)
		if kindIndex >= 0 && strings.Compare(typeName, name) == 0 {
			// extract specific type name from path
			extractedTypeName := path[kindIndex+len(kind)+1:]
			slashIndex := strings.IndexRune(extractedTypeName, '/')
			if slashIndex >= 0 {
				// we have a sub-package after the type we want to extract (this is the case for adapters)
				extractedTypeName = extractedTypeName[:slashIndex]
			}
			name = strings.Title(extractedTypeName)
			return name, true
		}

		return name, false
	}

	// transform type name if needed
	name, processed := nameTransformer("adapter", "Params")
	if !processed {
		name, _ = nameTransformer("template", "InstanceMsg")
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
			if name == "Time" {
				return "String"
			}
			if name == "BoolValue" {
				return "java.lang.Boolean"
			}
			if name == "Duration" {
				return "me.snowdrop.istio.api.model.Duration"
			}
			if name == "Value" {
				return "me.snowdrop.istio.api.model.v1.cexl.TypedValue"
			}
			if len(name) == 0 && t.NumField() == 0 {
				return "Object"
			}
			return name
		}
	}
}

func (g *schemaGenerator) generate(t reflect.Type) (*JSONSchema, error) {
	if t.Kind() != reflect.Struct {
		return nil, fmt.Errorf("Only struct types can be converted.")
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
			value := JSONPropertyDescriptor{
				JSONDescriptor: &JSONDescriptor{
					Type: "object",
				},
				JSONObjectDescriptor: v,
				JavaTypeDescriptor: &JavaTypeDescriptor{
					JavaType: g.javaType(k),
				},
			}
			s.Definitions[name] = value
			s.Resources[resource] = v
		}
	}

	if len(g.unknownEnums) > 0 {
		return &s, errors.New("Unknown enums: " + strings.Join(g.unknownEnums, ","))
	} else {
		return &s, nil
	}
}

func (g *schemaGenerator) getPropertyDescriptor(t reflect.Type, desc string) JSONPropertyDescriptor {
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
					Items: g.getPropertyDescriptor(t.Elem(), desc),
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
				MapValueType: g.getPropertyDescriptor(t.Elem(), desc),
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

		desc := getFieldDescription(field)
		enum := getFieldEnum(field)
		var prop JSONPropertyDescriptor
		if len(enum) > 0 {
			_, javaType := g.generateEnumTypeAccumulatingUnknown(enum)
			prop = JSONPropertyDescriptor{
				JavaTypeDescriptor: &JavaTypeDescriptor{
					JavaType: javaType,
				},
			}
		} else {
			prop = g.getPropertyDescriptor(field.Type, desc)
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
	case "Container", "Volume", "ContainePort", "ContainerStatus", "ServicePort", "EndpointPort":
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
