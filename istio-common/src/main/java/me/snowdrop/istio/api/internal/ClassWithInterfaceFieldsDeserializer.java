/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.api.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import static me.snowdrop.istio.api.internal.InterfacesRegistry.getFieldInfo;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsDeserializer extends JsonDeserializer implements ContextualDeserializer {
    private String targetClassName;

    private static final YAMLMapper mapper = new YAMLMapper();

    private static final TypeFactory factory = mapper.getTypeFactory();

    /*
     * Needed by Jackson
     */
    public ClassWithInterfaceFieldsDeserializer() {
    }

    ClassWithInterfaceFieldsDeserializer(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        Class targetClass;
        try {
            targetClass = Thread.currentThread().getContextClassLoader().loadClass(targetClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(targetClassName + " doesn't appear to be a known Istio class", e);
        }

        final Object result;
        try {
            result = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Couldn't create an instance of " + targetClassName, e);
        }

        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            final String fieldName = field.getKey();

            final InterfacesRegistry.FieldInfo info = getFieldInfo(targetClassName, fieldName);

            Object deserialized;
            final JsonNode value = field.getValue();

            switch (info.type()) {
                case "integer":
                    deserialized = value.intValue();
                    break;
                case "string":
                    deserialized = value.textValue();
                    break;
                case "number":
                    deserialized = value.doubleValue();
                    break;
                case "boolean":
                    deserialized = value.booleanValue();
                    break;
                default:
                    if (info instanceof InterfacesRegistry.MapFieldInfo) {
                        InterfacesRegistry.MapFieldInfo mapFieldInfo = (InterfacesRegistry.MapFieldInfo) info;
                        // deal with map types
                        final String valueType = mapFieldInfo.valueType();
                        final String type = getFieldClassFQN(targetClass, valueType);
                        try {
                            // load class of the field
                            final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(type);
                            // create a map type matching the type of the field from the mapping information
                            MapType mapType = factory.constructMapType(Map.class, String.class, fieldClass);
                            // get a parser taking the current value as root
                            final JsonParser traverse = value.traverse(p.getCodec());
                            // and use it to deserialize the subtree as the map type we just created
                            deserialized = mapper.readValue(traverse, mapType);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Unsupported type '" + type + "' for field '" + fieldName +
                                    "' on '" + targetClassName + "' class. Full type was " + mapFieldInfo, e);
                        }
                    } else {
                        final String type = getFieldClassFQN(targetClass, info.type());
                        try {
                            final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(type);
                            final ObjectNode targetNode = info instanceof InterfacesRegistry.InterfaceFieldInfo ? node :
                                    (ObjectNode) node.get(fieldName);
                            deserialized = p.getCodec().treeToValue(targetNode, fieldClass);
                        } catch (ClassNotFoundException | JsonProcessingException e) {
                            throw new RuntimeException("Unsupported type '" + type + "' for field '" + fieldName + "' on '" + targetClassName + "' class", e);
                        }
                    }
            }

            try {
                final Field targetClassField = targetClass.getDeclaredField(info.target());
                targetClassField.setAccessible(true);
                targetClassField.set(result, deserialized);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't assign '" + deserialized + "' to '" + info.target()
                        + "' target field on '" + targetClassName + "' class", e);
            }
        }

        return result;
    }

    private String getFieldClassFQN(Class targetClass, String type) {
        // if type contains a '.', we have a fully qualified target type so use it, otherwise use the target
        // class package
        return type.contains(".") ? type : targetClass.getPackage().getName() + '.' + type;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final Class<?> classToDeserialize = property != null ? property.getType().getRawClass() :
                ctxt.getContextualType().getRawClass();
        return new ClassWithInterfaceFieldsDeserializer(classToDeserialize.getName());
    }


}
