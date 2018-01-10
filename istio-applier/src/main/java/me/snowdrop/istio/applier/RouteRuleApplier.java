package me.snowdrop.istio.applier;

import java.util.Map;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.v1.routing.DoneableRouteRule;
import me.snowdrop.istio.api.model.v1.routing.RouteRule;

public class RouteRuleApplier implements Applier {

    private static final String ROUTE_RULE_KIND = "RouteRule";
    static final String ROUTE_RULE_CUSTOM_RESOURCE_DEFINITION = "routerules.config.istio.io";

    private final Adapter adapter;

    public RouteRuleApplier(Adapter adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public boolean canApply(String kind) {
        return ROUTE_RULE_KIND.equals(kind);
    }

    @Override
    public IstioResource apply(Map<String, Object> resource) {

        final RouteRule routeRule = ObjectMapperFactory.objectMapper.convertValue(resource, RouteRule.class);
        return this.adapter.createCustomResource(ROUTE_RULE_CUSTOM_RESOURCE_DEFINITION, routeRule, DoneableRouteRule.class);

    }
}
