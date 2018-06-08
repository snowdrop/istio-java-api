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
import io.fabric8.kubernetes.api.model.ListMeta;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioResourceList;
import me.snowdrop.istio.api.model.IstioSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.resolveIstioSpecForKind;

/**
 * @author
 */
public class IstioListDeserializer extends JsonDeserializer<IstioResourceList> {
    private static final String KIND = "kind";


    @Override
    public IstioResourceList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        JsonNode kindNode = node.get(KIND);
        if (kindNode != null) {
            final String kind = kindNode.textValue();

            final String apiVersion = node.get("apiVersion").textValue();

            // deserialize metadata
            final JsonNode metadataNode = node.get("metadata");
            final ListMeta metadata = p.getCodec().treeToValue(metadataNode, ListMeta.class);

            // deserialize items
            final JsonNode itemsNode = node.get("items");

            final ArrayList<IstioResource> items = new ArrayList<IstioResource>();
            for (final JsonNode elementNode: itemsNode) {
                IstioResource resource = p.getCodec().treeToValue(elementNode, IstioResource.class);
                items.add(resource);
            }

            return new IstioResourceList(apiVersion, items, kind, metadata);
        }
        throw new IllegalArgumentException("Cannot process resources without a 'kind' field");
    }
}
