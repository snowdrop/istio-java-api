/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.snowdrop.istio.api.model.v1.cexl.TypedValue;
import me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class TypedValueMapDeserializer extends JsonDeserializer<Map<String, TypedValue>> {
    @Override
    public Map<String, TypedValue> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        ObjectNode root = codec.readTree(p);

        final int size = root.size();
        if (size > 0) {
            final Map<String, TypedValue> values = new HashMap<>(size);
            // todo: parse expression and determine type instead of hardcoding ValueType.STRING
            root.fields().forEachRemaining(field -> values.put(field.getKey(), new TypedValue(ValueType.STRING, field.getValue().textValue())));
            return values;
        } else {
            return null;
        }
    }
}
