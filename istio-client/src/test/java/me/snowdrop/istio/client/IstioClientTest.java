package me.snowdrop.istio.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.snowdrop.istio.api.internal.IstioSpecRegistry;
import me.snowdrop.istio.api.model.IstioResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getCRDNameFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
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
        when(adapter.createCustomResources(any(IstioResource.class))).thenReturn(Collections.singletonList(new IstioResource()));

        // Then
        final List<IstioResource> istioResource = istioExecutor.registerCustomResources(routeRule);
        assertThat(istioResource).isNotEmpty();
        assertThat(istioResource.size()).isEqualTo(1);
        verify(adapter, times(1)).createCustomResources(any(IstioResource.class));

    }

    @Test
    public void shouldApplyDestinationPolicyIstioResource() {

        // Given
        final IstioClient istioExecutor = new IstioClient(adapter);
        final InputStream destinationPolicy = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("destination-policy.yaml");

        // When
        when(adapter.createCustomResources(any(IstioResource.class))).thenReturn(Collections.singletonList(new IstioResource()));

        // Then
        final List<IstioResource> istioResource = istioExecutor.registerCustomResources(destinationPolicy);
        assertThat(istioResource).isNotEmpty();
        assertThat(istioResource.size()).isEqualTo(1);
        verify(adapter, times(1)).createCustomResources(any(IstioResource.class));

    }

    @Test
    public void shouldApplyEgressRuleIstioResource() {

        // Given
        final IstioClient istioExecutor = new IstioClient(adapter);
        final InputStream egressRule = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("egress-rule.yaml");

        // When
        when(adapter.createCustomResources(any(IstioResource.class))).thenReturn(Collections.singletonList(new IstioResource()));

        // Then
        final List<IstioResource> istioResource = istioExecutor.registerCustomResources(egressRule);
        assertThat(istioResource).isNotEmpty();
        assertThat(istioResource.size()).isEqualTo(1);
        verify(adapter, times(1)).createCustomResources(any(IstioResource.class));

    }

}
