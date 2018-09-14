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
import java.util.List;
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
    private static Map<String, Map<String, String>> classNameToFieldInfos = new HashMap<>();

    static {
        YAMLMapper mapper = new YAMLMapper();
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("interfaces-data.yml");
        try {
            final Classes classes = mapper.readValue(dataIs, Classes.class);
            classes.classes.forEach(ci -> classNameToFieldInfos.put(ci.className, ci.fields));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> fieldToType;

    private String targetClassName;

    /*
     * Needed by Jackson
     */
    public ClassWithInterfaceFieldsDeserializer() {
    }

    private ClassWithInterfaceFieldsDeserializer(String targetClassName, Map<String, String> fieldNameToClass) {
        this.targetClassName = targetClassName;
        this.fieldToType = fieldNameToClass;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        Class targetClass = null;
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

            final String declaredType = fieldToType.get(fieldName);
            if (declaredType == null) {
                throw new IllegalArgumentException("Unknown field '" + fieldName + "'");
            }

            final FieldInfo info = getFieldInfo(fieldName, declaredType);

            Object deserialized;
            final JsonNode value = field.getValue();

            switch (info.type) {
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
                    final String type = targetClass.getPackage().getName() + '.' + info.type;
                    try {
                        final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(type);
                        deserialized = p.getCodec().treeToValue(node, fieldClass);
                    } catch (ClassNotFoundException | JsonProcessingException e) {
                        throw new RuntimeException("Unsupported type '" + type + "' for field '" + fieldName + "' on '" + targetClassName + "' class", e);
                    }
            }

            try {
                final Field targetClassField = targetClass.getDeclaredField(info.target);
                targetClassField.setAccessible(true);
                targetClassField.set(result, deserialized);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't assign '" + deserialized + "' to '" + info.target
                        + "' target field on '" + targetClassName + "' class", e);
            }
        }

        return result;
    }

    /**
     * percent, integer => percent, integer
     * grpcStatus, isHTTPFaultInjection_Abort_ErrorType => errorType, GrpcStatusErrorType
     *
     * @param fieldName
     * @param type
     * @return
     */
    private static FieldInfo getFieldInfo(String fieldName, String type) {
        if (type.startsWith("is")) {
            final String interfaceName = type.substring(type.lastIndexOf('_') + 1);
            final String target = interfaceName.substring(0, 1).toLowerCase() + interfaceName.substring(1);
            final String impl = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            return new FieldInfo(target, impl + interfaceName);
        } else {
            return new FieldInfo(fieldName, type);
        }
    }

    private static class Classes {
        @JsonProperty
        private List<ClassInfo> classes;
    }

    private static class ClassInfo {
        @JsonProperty("class")
        private String className;

        @JsonProperty
        private Map<String, String> fields;
    }

    private static class FieldInfo {
        private final String target;

        private final String type;

        public FieldInfo(String target, String type) {
            this.target = target;
            this.type = type;
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final Class<?> classToDeserialize = property.getType().getRawClass();
        final Map<String, String> fieldInfoMap = classNameToFieldInfos.get(classToDeserialize.getName());

        return new ClassWithInterfaceFieldsDeserializer(classToDeserialize.getName(), fieldInfoMap);
    }
}
