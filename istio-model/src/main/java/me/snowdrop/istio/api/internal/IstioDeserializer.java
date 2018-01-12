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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import me.snowdrop.istio.api.model.IstioBaseResource;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioSpec;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioDeserializer extends JsonDeserializer<IstioResource> {
    private static final String KIND = "kind";

    private static final Map<String, Class<? extends IstioSpec>> MAP = new HashMap<>();

    private static final String ISTIO_PACKAGE_PREFIX = "me.snowdrop.istio.api.model.";
    private static final String ISTIO_VERSION = "v1";
    private static final String ISTIO_BROKER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + ".broker.";
    private static final String ISTIO_MESH_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + ".mesh.";
    private static final String ISTIO_MIXER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + ".mixer.";
    private static final String ISTIO_ROUTING_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + ".routing.";


    @Override
    public IstioResource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        JsonNode kindNode = node.get(KIND);
        if (kindNode != null) {
            final String kind = kindNode.textValue();

            // find the associated spec class
            Class<? extends IstioSpec> specType = getTypeForName(kind);
            if (specType == null) {
                throw ctxt.mappingException(String.format("No resource type found for kind:%s", kind));
            }

            final String apiVersion = node.get("apiVersion").textValue();

            // deserialize metadata
            final JsonNode metadataNode = node.get("metadata");
            final ObjectMeta metadata = p.getCodec().treeToValue(metadataNode, ObjectMeta.class);

            // deserialize spec
            final JsonNode specNode = node.get("spec");
            final IstioSpec spec = p.getCodec().treeToValue(specNode, specType);


            return new IstioBaseResource(apiVersion, kind, metadata, spec);
        }
        throw new IllegalArgumentException("Cannot process resources without a 'kind' field");
    }

    private static Class getTypeForName(String name) {
        Class result = MAP.get(name);
        if (result == null) {
            result = loadClassIfExists(ISTIO_PACKAGE_PREFIX + name);
            if (result == null) {
                result = loadClassIfExists(ISTIO_ROUTING_PACKAGE_PREFIX + name);
                if (result == null) {
                    result = loadClassIfExists(ISTIO_BROKER_PACKAGE_PREFIX + name);
                    if (result == null) {
                        result = loadClassIfExists(ISTIO_MIXER_PACKAGE_PREFIX + name);
                        if (result == null) {
                            result = loadClassIfExists(ISTIO_MESH_PACKAGE_PREFIX + name);
                        }
                    }
                }
            }
        }

        if (result != null) {
            MAP.put(name, result);
        }
        return result;
    }

    private static Class loadClassIfExists(String className) {
        try {
            return IstioDeserializer.class.getClassLoader().loadClass(className);
        } catch (Throwable t) {
            return null;
        }
    }
}
