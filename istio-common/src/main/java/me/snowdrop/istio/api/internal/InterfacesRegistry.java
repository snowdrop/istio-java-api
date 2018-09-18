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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class InterfacesRegistry {
    private static Map<String, Map<String, String>> classNameToFieldInfos = new HashMap<>();

    static {
        // load interfaces information
        YAMLMapper mapper = new YAMLMapper();
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("interfaces-data.yml");
        try {
            final Classes classes = mapper.readValue(dataIs, Classes.class);
            classes.classes.forEach(ci -> classNameToFieldInfos.put(ci.className, ci.fields));
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
        final String type = classNameToFieldInfos.getOrDefault(targetClassName, Collections.emptyMap()).get(fieldName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown field '" + fieldName + "'");
        }

        if (type.startsWith("is")) {
            final String interfaceName = type.substring(type.lastIndexOf('_') + 1);
            final String target = interfaceName.substring(0, 1).toLowerCase() + interfaceName.substring(1);
            final String impl = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            return new InterfaceFieldInfo(target, impl + interfaceName);
        } else if (type.startsWith("map")) {
            return new MapFieldInfo(fieldName, type);
        } else {
            return new FieldInfo(fieldName, type);
        }
    }

    static Set<String> getKnownClasses() {
        return Collections.unmodifiableSet(classNameToFieldInfos.keySet());
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
    }

    static class InterfaceFieldInfo extends FieldInfo {
        private InterfaceFieldInfo(String target, String type) {
            super(target, type);
        }
    }
}
