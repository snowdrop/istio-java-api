package me.snowdrop.istio.applier;

import java.util.Map;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.v1.routing.DoneableEgressRule;
import me.snowdrop.istio.api.model.v1.routing.EgressRule;

public class EgressRuleApplier implements Applier {

    private static final String EGRESS_RULE_KIND = "EgressRule";
    static final String EGRESS_RULE_CUSTOM_RESOURCE_DEFINITION = "egressrules.config.istio.io";

    private final Adapter adapter;

    public EgressRuleApplier(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean canApply(String kind) {
        return EGRESS_RULE_KIND.equals(kind);
    }

    @Override
    public IstioResource apply(Map<String, Object> resource) {

        final EgressRule
            egressRule = ObjectMapperFactory.objectMapper.convertValue(resource, EgressRule.class);
        return this.adapter.createCustomResource(EGRESS_RULE_CUSTOM_RESOURCE_DEFINITION, egressRule, DoneableEgressRule.class);
    }

}
