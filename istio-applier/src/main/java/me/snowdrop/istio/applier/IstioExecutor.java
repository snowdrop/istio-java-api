package me.snowdrop.istio.applier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import me.snowdrop.istio.api.model.v1.routing.DestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.DoneableDestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.EgressRule;
import me.snowdrop.istio.api.model.v1.routing.DoneableEgressRule;
import me.snowdrop.istio.api.model.v1.routing.RouteRule;
import me.snowdrop.istio.api.model.v1.routing.DoneableRouteRule;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.snowdrop.istio.api.model.IstioResource;

public class IstioExecutor {
    public static final String ROUTE_RULE_CRD_NAME = "routerules.config.istio.io";
    public static final String DESTINATION_POLICY_CRD_NAME = "destinationpolicies.config.istio.io";
    public static final String EGRESS_RULE_CRD_NAME = "egressrules.config.istio.io";

    private final static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private static final String KIND = "kind";
    private static final Map<String, Applier<? extends IstioResource>> knownResources = new ConcurrentHashMap<>();
    private final Adapter client;

    public IstioExecutor(Adapter client) {
        this.client = client;

        final Applier<RouteRule> routeRuleApplier = new GenericApplier<>("RouteRule", ROUTE_RULE_CRD_NAME, RouteRule.class, DoneableRouteRule.class);
        final Applier<DestinationPolicy> destinationPolicyApplier = new GenericApplier<>("DestinationPolicy", DESTINATION_POLICY_CRD_NAME, DestinationPolicy.class, DoneableDestinationPolicy.class);
        final Applier<EgressRule> egressRuleApplier = new GenericApplier<>("EgressRule", EGRESS_RULE_CRD_NAME, EgressRule.class, DoneableEgressRule.class);


        knownResources.put(routeRuleApplier.getKind(), routeRuleApplier);
        knownResources.put(destinationPolicyApplier.getKind(), destinationPolicyApplier);
        knownResources.put(egressRuleApplier.getKind(), egressRuleApplier);
    }

    public static Applier getApplierFor(String kind) {
        return knownResources.get(kind);
    }

    public static <T extends IstioResource> Applier<T> getApplierFor(Class<T> resourceClass) {
        // simple logic should work for now but might not handle trickier cases
        return resourceClass == null ? null : getApplierFor(resourceClass.getSimpleName());
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

    @SuppressWarnings("unchecked")
    private Optional<IstioResource> apply(Map<String, Object> resourceYaml) {
        if (resourceYaml.containsKey(KIND)) {

            final String kind = (String) resourceYaml.get(KIND);
            final Applier<? extends IstioResource> applier = knownResources.get(kind);

            if (applier != null) {
                final IstioResource resource = objectMapper.convertValue(resourceYaml, applier.getResourceClass());

                return Optional.of(client.createCustomResource(resource, (Applier<IstioResource>) applier));
            } else {
                throw new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
        }
    }
}
