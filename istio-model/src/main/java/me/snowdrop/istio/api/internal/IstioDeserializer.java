/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.internal;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.resolveIstioSpecForKind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.io.IOException;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioSpec;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioDeserializer extends JsonDeserializer<IstioResource> {
    private static final String KIND = "kind";


    @Override
    public IstioResource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        JsonNode kindNode = node.get(KIND);
        if (kindNode != null) {
            final String kind = kindNode.textValue();

            // find the associated spec class
            Class<? extends IstioSpec> specType = resolveIstioSpecForKind(kind);
            if (specType == null) {
                throw ctxt.mappingException(String.format("No resource type found for kind: %s", kind));
            }

            final String apiVersion = node.get("apiVersion").textValue();

            // deserialize metadata
            final JsonNode metadataNode = node.get("metadata");
            final ObjectMeta metadata = p.getCodec().treeToValue(metadataNode, ObjectMeta.class);

            return new IstioResource(apiVersion, kind, metadata, getIstioSpec(p, node, specType));
        }
        throw new IllegalArgumentException("Cannot process resources without a 'kind' field");
    }

    private IstioSpec getIstioSpec(JsonParser p, ObjectNode node,
        Class<? extends IstioSpec> specType) throws JsonProcessingException {

        final JsonNode specNode = node.get("spec");
        return specNode != null ? p.getCodec().treeToValue(specNode, specType) : null;
    }
}
