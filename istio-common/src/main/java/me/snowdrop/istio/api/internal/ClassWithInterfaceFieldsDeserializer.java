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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsDeserializer extends JsonDeserializer {
    private Map<String, FieldInfo> fieldNameToClass = new HashMap<>();

    private String targetClassName = "me.snowdrop.istio.api.model.v1.networking.Abort";

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        fieldNameToClass.put("percent", new FieldInfo("percent", "percent", "integer"));
        fieldNameToClass.put("grpcStatus", new FieldInfo("grpcStatus", "errorType",
                "me.snowdrop.istio.api.model.v1.networking.GrpcStatusAbortHTTPFaultInjection"));
        fieldNameToClass.put("httpStatus", new FieldInfo("httpStatus", "errorType",
                "me.snowdrop.istio.api.model.v1.networking.HttpStatusAbortHTTPFaultInjection"));
        fieldNameToClass.put("http2Error", new FieldInfo("http2Error", "errorType",
                "me.snowdrop.istio.api.model.v1.networking.Http2ErrorAbortHTTPFaultInjection"));

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
                    final Field targetClassField = targetClass.getDeclaredField(fieldInfo.targetFieldName);
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

    private static class FieldInfo {
        final String name;

        final String targetFieldName;

        final String typeName;

        FieldInfo(String name, String targetFieldName, String typeName) {
            this.name = name;
            this.typeName = typeName;
            this.targetFieldName = targetFieldName;
        }

        boolean isSimpleField() {
            return name.equals(targetFieldName);
        }
    }
}
