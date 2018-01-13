package me.snowdrop.istio.applier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.snowdrop.istio.api.model.IstioResource;

public class IstioExecutor {
    public static final String DESTINATION_POLICY_CRD_NAME = "destinationpolicies.config.istio.io";
    public static final String EGRESS_RULE_CRD_NAME = "egressrules.config.istio.io";
    public static final String ROUTE_RULE_CRD_NAME = "routerules.config.istio.io";

    private final static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static final String KIND = "kind";
    private static final Map<String, String> kindToCRD = new ConcurrentHashMap<>();

    static {
        kindToCRD.put("DestinationPolicy", DESTINATION_POLICY_CRD_NAME);
        kindToCRD.put("EgressRule", EGRESS_RULE_CRD_NAME);
        kindToCRD.put("RouteRule", ROUTE_RULE_CRD_NAME);
    }

    private final Adapter client;

    public IstioExecutor(Adapter client) {
        this.client = client;
    }

    public static String getCRDNameFor(String kind) {
        return kindToCRD.get(kind);
    }

    public Optional<IstioResource> registerCustomResource(final String resource) {
        try {
            final Map<String, Object> resourceYaml = objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Optional<IstioResource> registerCustomResource(final InputStream resource) {
        try {
            final Map<String, Object> resourceYaml = objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Optional<IstioResource> registerCustomResource(final IstioResource resource) {
        final String kind = resource.getKind();
        final String crdName = getCRDNameFor(kind);
        if (crdName != null) {
            return Optional.of(client.createCustomResource(crdName, resource));
        } else {
            throw new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind));
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<IstioResource> apply(Map<String, Object> resourceYaml) {
        if (resourceYaml.containsKey(KIND)) {
            final String kind = (String) resourceYaml.get(KIND);
            final String crdName = getCRDNameFor(kind);
            if (crdName != null) {
                final IstioResource resource = objectMapper.convertValue(resourceYaml, IstioResource.class);

                return Optional.of(client.createCustomResource(crdName, resource));
            } else {
                throw new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
        }
    }
}
