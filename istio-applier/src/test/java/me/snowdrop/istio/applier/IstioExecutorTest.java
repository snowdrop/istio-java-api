package me.snowdrop.istio.applier;

import java.io.InputStream;
import java.util.Optional;

import me.snowdrop.istio.api.model.IstioResource;
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
        when(adapter.createCustomResource(eq(IstioExecutor.getCRDNameFor("RouteRule")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioExecutor.getCRDNameFor("RouteRule")), any(IstioResource.class));

    }

    @Test
    public void should_apply_destination_policy_istio_resource() {

        // Given
        final IstioExecutor istioExecutor = new IstioExecutor(adapter);
        final InputStream destinationPolicy = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("destination-policy.yaml");

        // When
        when(adapter.createCustomResource(eq(IstioExecutor.getCRDNameFor("DestinationPolicy")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(destinationPolicy);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioExecutor.getCRDNameFor("DestinationPolicy")), any(IstioResource.class));

    }

    @Test
    public void should_apply_egress_rule_istio_resource() {

        // Given
        final IstioExecutor istioExecutor = new IstioExecutor(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("egress-rule.yaml");

        // When
        when(adapter.createCustomResource(eq(IstioExecutor.getCRDNameFor("EgressRule")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioExecutor.getCRDNameFor("EgressRule")), any(IstioResource.class));

    }

}
