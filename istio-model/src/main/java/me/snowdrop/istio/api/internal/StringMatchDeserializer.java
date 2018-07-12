/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.internal;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.snowdrop.istio.api.model.v1.networking.ExactStringMatch;
import me.snowdrop.istio.api.model.v1.networking.PrefixStringMatch;
import me.snowdrop.istio.api.model.v1.networking.RegexStringMatch;
import me.snowdrop.istio.api.model.v1.networking.StringMatch;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class StringMatchDeserializer extends JsonDeserializer<StringMatch> {
    @Override
    public StringMatch deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectNode node = jsonParser.readValueAsTree();

        // there should be exactly one field
        final Map.Entry<String, JsonNode> field = node.fields().next();
        final String value = field.getValue().asText();
        switch (field.getKey()) {
            case ExactStringMatch.MATCH_TYPE:
                return new ExactStringMatch(value);
            case PrefixStringMatch.MATCH_TYPE:
                return new PrefixStringMatch(value);
            case RegexStringMatch.MATCH_TYPE:
                return new RegexStringMatch(value);
            default:
                throw new IllegalArgumentException("Unknown StringMatch type: " + field.getKey());
        }
    }
}
