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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static me.snowdrop.istio.api.internal.ClassWithInterfaceFieldsRegistry.getFieldInfo;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsDeserializer extends JsonDeserializer implements ContextualDeserializer {
    private String targetClassName;

    /*
     * Needed by Jackson
     */
    public ClassWithInterfaceFieldsDeserializer() {
    }

    private ClassWithInterfaceFieldsDeserializer(String targetClassName) {
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

            final ClassWithInterfaceFieldsRegistry.FieldInfo info = getFieldInfo(targetClassName, fieldName);

            Object deserialized = info.deserialize(node, fieldName, targetClass, ctxt);

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

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final Class<?> classToDeserialize;
        if (property != null) {
            final JavaType type = property.getType();
            classToDeserialize = type.isContainerType() ? type.getContentType().getRawClass() : type.getRawClass();
        } else {
            classToDeserialize = ctxt.getContextualType().getRawClass();
        }

        return new ClassWithInterfaceFieldsDeserializer(classToDeserialize.getName());
    }


}
