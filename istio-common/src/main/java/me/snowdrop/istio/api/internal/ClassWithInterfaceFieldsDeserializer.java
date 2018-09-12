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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsDeserializer extends JsonDeserializer implements ContextualDeserializer {
    private static Map<String, Map<String, FieldInfo>> classNameToFieldInfos = new HashMap<>();

    static {
        YAMLMapper mapper = new YAMLMapper();
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("interfaces-data.yml");
        try {
            final Map<String, Map> map = mapper.readValue(dataIs, Map.class);
            map.forEach((s, o) -> {
                final Map<String, Object> fields = mapper.convertValue(o, Map.class);
                final Map<String, FieldInfo> infos = new HashMap<>(fields.size());
                fields.forEach((o1, o2) -> {
                    infos.put(o1, mapper.convertValue(o2, FieldInfo.class));
                });

                classNameToFieldInfos.put(s, infos);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, FieldInfo> fieldNameToClass;

    private String targetClassName;

    /*
     * Needed by Jackson
     */
    public ClassWithInterfaceFieldsDeserializer() {
    }

    private ClassWithInterfaceFieldsDeserializer(String targetClassName, Map<String, FieldInfo> fieldNameToClass) {
        this.targetClassName = targetClassName;
        this.fieldNameToClass = fieldNameToClass;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        Class targetClass = null;
        try {
            targetClass = Thread.currentThread().getContextClassLoader().loadClass(targetClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final Object result;
        try {
            result = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            final String fieldName = field.getKey();

            final FieldInfo fieldInfo = fieldNameToClass.get(fieldName);
            if (fieldInfo == null) {
                throw new IllegalArgumentException("Unknown field '" + fieldName + "'");
            }

            try {

                Object deserialized;
                final JsonNode value = field.getValue();
                if (fieldInfo.isSimpleField()) {
                    switch (fieldInfo.typeName) {
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
                            throw new RuntimeException("Unsupported type " + fieldInfo.typeName + " for field " + fieldName + " on " +
                                    "class " + targetClassName);
                    }
                } else {
                    final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(fieldInfo.typeName);
                    deserialized = p.getCodec().treeToValue(node, fieldClass);
                }


                try {
                    final Field targetClassField = targetClass.getDeclaredField(getTargetFieldName(fieldName, fieldInfo));
                    targetClassField.setAccessible(true);
                    targetClassField.set(result, deserialized);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        return result;
    }

    private String getTargetFieldName(String fieldName, FieldInfo fieldInfo) {
        return fieldInfo.targetFieldName != null ? fieldInfo.targetFieldName : fieldName;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final Class<?> classToDeserialize = property.getType().getRawClass();
        final Map<String, FieldInfo> fieldInfoMap = classNameToFieldInfos.get(classToDeserialize.getName());

        return new ClassWithInterfaceFieldsDeserializer(classToDeserialize.getName(), fieldInfoMap);
    }

    private static class FieldInfo {
        @JsonProperty("target")
        String targetFieldName;

        @JsonProperty("type")
        String typeName;

        boolean isSimpleField() {
            return targetFieldName != null;
        }
    }
}
