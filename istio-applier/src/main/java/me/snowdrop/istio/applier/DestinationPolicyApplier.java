package me.snowdrop.istio.applier;

import java.util.Map;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.v1.routing.DestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.DoneableDestinationPolicy;

public class DestinationPolicyApplier implements Applier {

    private static final String DESTINATION_POLICY_KIND = "DestinationPolicy";
    static final String DESTINATION_POLICY_CUSTOM_RESOURCE_DEFINITION = "destinationpolicies.config.istio.io";

    private final Adapter adapter;

    public DestinationPolicyApplier(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean canApply(String kind) {
        return DESTINATION_POLICY_KIND.equals(kind);
    }

    @Override
    public IstioResource apply(Map<String, Object> resource) {

        final DestinationPolicy destinationPolicy = ObjectMapperFactory.objectMapper.convertValue(resource, DestinationPolicy.class);
        return this.adapter.createCustomResource(DESTINATION_POLICY_CUSTOM_RESOURCE_DEFINITION, destinationPolicy, DoneableDestinationPolicy.class);
    }
}
