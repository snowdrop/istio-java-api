package me.snowdrop.istio.applier;

import java.io.InputStream;
import java.util.Optional;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.v1.routing.DestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.DoneableDestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.DoneableEgressRule;
import me.snowdrop.istio.api.model.v1.routing.DoneableRouteRule;
import me.snowdrop.istio.api.model.v1.routing.EgressRule;
import me.snowdrop.istio.api.model.v1.routing.RouteRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IstioExecutorTest {

    @Mock
    Adapter adapter;

    @Test
    public void should_apply_route_rule_istio_resource() {

        // Given
        final IstioExecutor istioExecutor = new IstioExecutor(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("route-rule.yaml");

        // When
        when(adapter.createCustomResource(eq(RouteRuleApplier.ROUTE_RULE_CUSTOM_RESOURCE_DEFINITION), any(RouteRule.class),
            eq(DoneableRouteRule.class))).thenReturn(new RouteRule());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(RouteRuleApplier.ROUTE_RULE_CUSTOM_RESOURCE_DEFINITION), any(RouteRule.class),
            eq(DoneableRouteRule.class));

    }

    @Test
    public void should_apply_destination_policy_istio_resource() {

        // Given
        final IstioExecutor istioExecutor = new IstioExecutor(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("destination-policy.yaml");

        // When
        when(adapter.createCustomResource(eq(DestinationPolicyApplier.DESTINATION_POLICY_CUSTOM_RESOURCE_DEFINITION), any(DestinationPolicy.class),
            eq(DoneableDestinationPolicy.class))).thenReturn(new DestinationPolicy());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(DestinationPolicyApplier.DESTINATION_POLICY_CUSTOM_RESOURCE_DEFINITION), any(DestinationPolicy.class),
            eq(DoneableDestinationPolicy.class));

    }

    @Test
    public void should_apply_egress_rule_istio_resource() {

        // Given
        final IstioExecutor istioExecutor = new IstioExecutor(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("egress-rule.yaml");

        // When
        when(adapter.createCustomResource(eq(EgressRuleApplier.EGRESS_RULE_CUSTOM_RESOURCE_DEFINITION), any(EgressRule.class),
            eq(DoneableEgressRule.class))).thenReturn(new EgressRule());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(EgressRuleApplier.EGRESS_RULE_CUSTOM_RESOURCE_DEFINITION), any(EgressRule.class),
            eq(DoneableEgressRule.class));

    }

}
