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
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.snowdrop.istio.api.model.v1.networking.ConsistentHashLoadBalancerSettings;
import me.snowdrop.istio.api.model.v1.networking.LoadBalancerSettings;
import me.snowdrop.istio.api.model.v1.networking.SimpleLoadBalancerSettings;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class LoadBalancerSettingsDeserializer extends JsonDeserializer<LoadBalancerSettings> {
    @Override
    public LoadBalancerSettings deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectNode node = jsonParser.readValueAsTree();

        final Map.Entry<String, JsonNode> field = node.fields().next();
        final String fieldName = field.getKey();
        switch (fieldName) {
            case "simple":
                return jsonParser.getCodec().treeToValue(node, SimpleLoadBalancerSettings.class);
            case "consistentHash":
                return jsonParser.getCodec().treeToValue(node, ConsistentHashLoadBalancerSettings.class);
            default:
                throw new IllegalArgumentException("Unknown LoadBalancerSettings type: " + fieldName);
        }
    }
}
