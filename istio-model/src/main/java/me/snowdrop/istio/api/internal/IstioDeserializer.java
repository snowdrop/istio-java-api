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
    private static final String ISTIO_PACKAGE_PREFIX = "me.snowdrop.istio.api.model.";
    private static final String ISTIO_VERSION = "v1.";
    private static final String ISTIO_MIXER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + "mixer.";
    private static final String ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX = ISTIO_MIXER_PACKAGE_PREFIX + "template.";
    private static final String ISTIO_ROUTING_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + "routing.";


    private static final Map<String, Class<? extends IstioSpec>> KIND_TO_TYPE = new HashMap<>();
    private static final Map<String, String> KIND_TO_CLASSNAME = new HashMap<>();

    static {
        KIND_TO_CLASSNAME.put("RouteRule", ISTIO_ROUTING_PACKAGE_PREFIX + "RouteRule");
        KIND_TO_CLASSNAME.put("DestinationPolicy", ISTIO_ROUTING_PACKAGE_PREFIX + "DestinationPolicy");
        KIND_TO_CLASSNAME.put("EgressRule", ISTIO_ROUTING_PACKAGE_PREFIX + "EgressRule");
        KIND_TO_CLASSNAME.put("checknothing", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "CheckNothing");
        KIND_TO_CLASSNAME.put("listentry", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "ListEntry");
        KIND_TO_CLASSNAME.put("logentry", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "LogEntry");
        KIND_TO_CLASSNAME.put("quota", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "Quota");
    }


    @Override
    public IstioResource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectNode node = p.readValueAsTree();

        JsonNode kindNode = node.get(KIND);
        if (kindNode != null) {
            final String kind = kindNode.textValue();

            // find the associated spec class
            Class<? extends IstioSpec> specType = getTypeForName(kind);
            if (specType == null) {
                throw ctxt.mappingException(String.format("No resource type found for kind: %s", kind));
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
        Class result = KIND_TO_TYPE.get(name);
        if (result == null) {
            final String className = KIND_TO_CLASSNAME.get(name);
            if (className != null) {
                result = loadClassIfExists(className);
                KIND_TO_TYPE.put(name, result);
            }
        }

        return result;
    }

    private static Class loadClassIfExists(String className) {
        try {
            return IstioDeserializer.class.getClassLoader().loadClass(className);
        } catch (Throwable t) {
            throw new IllegalArgumentException(String.format("Cannot load class: %s", className), t);
        }
    }
}
