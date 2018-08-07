package me.snowdrop.istio.client;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getKindFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioSpec;
import me.snowdrop.istio.api.model.v1.mixer.template.Metric;
import me.snowdrop.istio.api.model.v1.networking.VirtualService;
import me.snowdrop.istio.api.model.v1.policy.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IstioClientTest {

    @Mock
    Adapter adapter;

    @Test
    public void shouldApplyMetricIstioResource() {
        checkInput("metric.yaml", Metric.class);
    }

    @Test
    public void shouldApplyVirtualServiceIstioResource() {
        checkInput("virtual-service.yaml", VirtualService.class);
    }

    @Test
    public void shouldApplyruleIstioResource() {
        checkInput("rule.yaml", Rule.class);
    }

    private void checkInput(String inputFileName, Class<? extends IstioSpec> expectedSpecClass) {
        // Given
        final IstioClient client = new IstioClient(adapter);
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(inputFileName);

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
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getKind()).isEqualTo(getKindFor(Metric.class));
        assertThat(result.get(1).getKind()).isEqualTo(getKindFor(VirtualService.class));
        verify(adapter, times(1)).createCustomResources(any());
    }

}
