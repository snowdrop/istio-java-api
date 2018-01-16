package me.snowdrop.istio.client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioSpec;
import me.snowdrop.istio.api.model.v1.routing.DestinationPolicy;
import me.snowdrop.istio.api.model.v1.routing.EgressRule;
import me.snowdrop.istio.api.model.v1.routing.RouteRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getKindFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IstioClientTest {

    @Mock
    Adapter adapter;

    @Test
    public void shouldApplyRouteRuleIstioResource() {
        checkInput("route-rule.yaml", RouteRule.class);
    }

    @Test
    public void shouldApplyDestinationPolicyIstioResource() {
        checkInput("destination-policy.yaml", DestinationPolicy.class);
    }

    @Test
    public void shouldApplyEgressRuleIstioResource() {
        checkInput("egress-rule.yaml", EgressRule.class);
    }

    private void checkInput(String inputFileName, Class<? extends IstioSpec> expectedSpecClass) {
        // Given
        final IstioClient client = new IstioClient(adapter);
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(inputFileName);

        // When
        when(adapter.createCustomResources(any())).thenAnswer(invocation -> Collections.singletonList(invocation.getArgument(0)));

        // Then
        final List<IstioResource> result = client.registerCustomResources(inputStream);
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getKind()).isEqualTo(getKindFor(expectedSpecClass));
        verify(adapter, times(1)).createCustomResources(any(IstioResource.class));
    }

    @Test
    public void shouldApplyAllResourcesInAggregateDescriptor() {

        // Given
        final IstioClient client = new IstioClient(adapter);
        final InputStream aggregate = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("aggregate.yaml");


        // When
        when(adapter.createCustomResources(any())).thenAnswer(invocation -> Arrays.asList(invocation.getArguments()));

        // Then
        final List<IstioResource> result = client.registerCustomResources(aggregate);
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getKind()).isEqualTo(getKindFor(DestinationPolicy.class));
        assertThat(result.get(1).getKind()).isEqualTo(getKindFor(EgressRule.class));
        assertThat(result.get(2).getKind()).isEqualTo(getKindFor(RouteRule.class));
        verify(adapter, times(1)).createCustomResources(any());
    }

}
