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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsRegistry {
    private static final Map<String, Map<String, FieldInfo>> classNameToFieldInfos = new HashMap<>();

    private static final Set<String> supportedSimpleTypes = new HashSet<>();

    static {
        Collections.addAll(supportedSimpleTypes, "integer", "string", "number", "boolean");
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
    static {
        // load interfaces information
        YAMLMapper mapper = new YAMLMapper();
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("classes-with-interface-fields.yml");
        try {
            final Classes classes = mapper.readValue(dataIs, Classes.class);
            classes.classes.forEach(ci -> {
                final Map<String, FieldInfo> infos = new HashMap<>(ci.fields.size());
                for (Map.Entry<String, String> entry : ci.fields.entrySet()) {
                    final String type = entry.getValue();
                    final String fieldName = entry.getKey();

                    FieldInfo info;
                    if (supportedSimpleTypes.contains(type)) {
                        info = new FieldInfo(fieldName, type);
                    } else if (type.startsWith("is")) {
                        final String interfaceName = type.substring(type.lastIndexOf('_') + 1);
                        final String target = interfaceName.substring(0, 1).toLowerCase() + interfaceName.substring(1);
                        final String impl = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                        info = new InterfaceFieldInfo(target, impl + interfaceName);
                    } else if (type.startsWith("map")) {
                        info = new MapFieldInfo(fieldName, type);
                    } else {
                        info = new ObjectFieldInfo(fieldName, type);
                    }

                    infos.put(fieldName, info);
                }
                classNameToFieldInfos.put(ci.className, infos);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * percent, integer => percent, integer
     * grpcStatus, isHTTPFaultInjection_Abort_ErrorType => errorType, GrpcStatusErrorType
     *
     * @param targetClassName
     * @param fieldName
     * @return
     */
    static FieldInfo getFieldInfo(String targetClassName, String fieldName) {
        final FieldInfo info = classNameToFieldInfos.getOrDefault(targetClassName, Collections.emptyMap()).get(fieldName);
        if (info == null) {
            throw new IllegalArgumentException("Unknown field '" + fieldName + "'");
        }

        return info;
    }

    static Set<String> getKnownClasses() {
        return Collections.unmodifiableSet(classNameToFieldInfos.keySet());
    }

    static class FieldInfo {
        private final String target;

        private final String type;

        private FieldInfo(String target, String type) {
            this.target = target;
            this.type = type;
        }

        public String target() {
            return target;
        }

        public String type() {
            return type;
        }

        Object deserialize(JsonNode node, String fieldName, Class targetClass, DeserializationContext ctxt) throws IOException {
            final JsonNode value = node.get(fieldName);
            switch (type()) {
                case "integer":
                    return value.intValue();
                case "string":
                    return value.textValue();
                case "number":
                    return value.doubleValue();
                case "boolean":
                    return value.booleanValue();
                default:
                    throw new IllegalArgumentException("Unknown simple type '" + type + "'");
            }
        }
    }

    static class MapFieldInfo extends FieldInfo {
        private static final Pattern MAP_PATTERN = Pattern.compile("\\s*map<([^,]*),([^)]*)>");

        private final String keyType;

        private final String valueType;


        private MapFieldInfo(String target, String type) {
            super(target, type);

            final Matcher matcher = MAP_PATTERN.matcher(type);

            if (matcher.matches()) {
                keyType = matcher.group(1).trim();
                valueType = matcher.group(2).trim();
            } else {
                throw new IllegalArgumentException("Expected map field format 'map<T,U>', got: " + type);
            }
        }

        @Override
        public String toString() {
            return String.format("map<%s,%s>", keyType, valueType);
        }

        public String keyType() {
            return keyType;
        }

        public String valueType() {
            return valueType;
        }

        Object deserialize(JsonNode node, String fieldName, Class targetClass, DeserializationContext ctxt) throws IOException {
            final String type = getFieldClassFQN(targetClass, valueType);
            try {
                // load class of the field
                final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(type);
                // create a map type matching the type of the field from the mapping information
                final YAMLMapper codec = (YAMLMapper) ctxt.getParser().getCodec();
                MapType mapType = codec.getTypeFactory().constructMapType(Map.class, String.class, fieldClass);
                // get a parser taking the current value as root
                final JsonParser traverse = node.get(fieldName).traverse(codec);
                // and use it to deserialize the subtree as the map type we just created
                return codec.readValue(traverse, mapType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unsupported type '" + type + "' for field '" + fieldName +
                        "' on '" + targetClass.getName() + "' class. Full type was " + this, e);
            }
        }
    }

    static class ObjectFieldInfo extends FieldInfo {
        private ObjectFieldInfo(String target, String type) {
            super(target, type);
        }

        Object deserialize(JsonNode node, String fieldName, Class targetClass, DeserializationContext ctxt) throws IOException {
            final String type = getFieldClassFQN(targetClass, type());
            try {
                final Class<?> fieldClass = Thread.currentThread().getContextClassLoader().loadClass(type);
                return ctxt.getParser().getCodec().treeToValue(getTargetNode(node, fieldName), fieldClass);
            } catch (ClassNotFoundException | JsonProcessingException e) {
                throw new RuntimeException("Unsupported type '" + type + "' for field '" + fieldName + "' on '" + targetClass.getName() + "' class", e);
            }
        }

        protected JsonNode getTargetNode(JsonNode node, String fieldName) {
            return node.get(fieldName);
        }
    }

    static class InterfaceFieldInfo extends ObjectFieldInfo {
        private InterfaceFieldInfo(String target, String type) {
            super(target, type);
        }

        @Override
        protected JsonNode getTargetNode(JsonNode node, String fieldName) {
            return node;
        }
    }

    private static String getFieldClassFQN(Class targetClass, String type) {
        // if type contains a '.', we have a fully qualified target type so use it, otherwise use the target
        // class package
        return type.contains(".") ? type : targetClass.getPackage().getName() + '.' + type;
    }
}
