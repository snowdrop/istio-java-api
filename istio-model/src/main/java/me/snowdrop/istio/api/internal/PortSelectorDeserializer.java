/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.snowdrop.istio.api.model.v1.networking.*;

import java.io.IOException;
import java.util.Map;

public class PortSelectorDeserializer extends JsonDeserializer<PortSelector> {
    @Override
    public PortSelector deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectNode node = jsonParser.readValueAsTree();

        // there should be exactly one field
        final Map.Entry<String, JsonNode> field = node.fields().next();
        final int value = field.getValue().asInt();
        return new PortSelector(value);
    }
}
