package me.snowdrop.istio.client;

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
public class IstioClientTest {

    @Mock
    Adapter adapter;

    @Test
    public void shouldApplyRouteRuleIstioResource() {

        // Given
        final IstioClient istioExecutor = new IstioClient(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("route-rule.yaml");

        // When
        when(adapter.createCustomResource(eq(IstioClient.getCRDNameFor("RouteRule")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioClient.getCRDNameFor("RouteRule")), any(IstioResource.class));

    }

    @Test
    public void shouldApplyDestinationPolicyIstioResource() {

        // Given
        final IstioClient istioExecutor = new IstioClient(adapter);
        final InputStream destinationPolicy = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("destination-policy.yaml");

        // When
        when(adapter.createCustomResource(eq(IstioClient.getCRDNameFor("DestinationPolicy")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(destinationPolicy);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioClient.getCRDNameFor("DestinationPolicy")), any(IstioResource.class));

    }

    @Test
    public void shouldApplyEgressRuleIstioResource() {

        // Given
        final IstioClient istioExecutor = new IstioClient(adapter);
        final InputStream routeRule = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("egress-rule.yaml");

        // When
        when(adapter.createCustomResource(eq(IstioClient.getCRDNameFor("EgressRule")), any(IstioResource.class)))
                .thenReturn(new IstioResource());

        // Then
        final Optional<IstioResource> istioResource = istioExecutor.registerCustomResource(routeRule);

        assertThat(istioResource).isPresent();
        verify(adapter, times(1)).createCustomResource(eq(IstioClient.getCRDNameFor("EgressRule")), any(IstioResource.class));

    }

}
