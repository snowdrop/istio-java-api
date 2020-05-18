package me.snowdrop.istio.client.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.HashMap;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRule;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRuleBuilder;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRuleSpec;
import me.snowdrop.istio.api.networking.v1beta1.Subset;
import me.snowdrop.istio.api.networking.v1beta1.TLSSettingsMode;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Test;

public class DestinationRuleIT {

    private final IstioClient istioClient = new DefaultIstioClient();

    /*
    apiVersion: networking.istio.io/v1beta1
    kind: DestinationRule
    metadata:
      name: spring-boot-istio-routing-service
    spec:
      host: spring-boot-istio-routing-service
      trafficPolicy:
        tls:
          mode: DISABLE
      subsets:
      - name: a
        labels:
          version: a
      - name: b
        labels:
          version: b
     */
    @Test
    public void checkDestinationRuleWithTrafficPolicy() {
        //given
        final DestinationRule destinationRule = new DestinationRuleBuilder()
                .withApiVersion("networking.istio.io/v1beta1")
                .withNewMetadata()
                .withName("rule")
                .endMetadata()
                .withNewSpec()
                .withHost("somehost")
                .addNewSubset().withName("a").withLabels(new HashMap<String, String>() {{
                    put("version", "a");
                }}).endSubset()
                .addNewSubset().withName("b").withLabels(new HashMap<String, String>() {{
                    put("version", "b");
                }}).endSubset()
                .withNewTrafficPolicy()
                .withNewTls()
                .withMode(TLSSettingsMode.DISABLE)
                .endTls()
                .endTrafficPolicy()
                .endSpec()
                .build();

        //when
        final DestinationRule resultResource = istioClient.v1beta1DestinationRule().create(destinationRule);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("DestinationRule");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("rule");
        });

        //and
        final DestinationRuleSpec resultSpec = resultResource.getSpec();

        //and
        assertThat(resultSpec).satisfies(ps -> {

            assertThat(ps.getHost()).isEqualTo("somehost");
            assertThat(ps.getTrafficPolicy())
                .extracting("tls")
                .extracting("mode")
                .containsOnly(TLSSettingsMode.DISABLE);

            assertThat(ps.getSubsets())
                .extracting((Extractor<Subset, Object>) subset ->
                  tuple(subset.getName(), subset.getLabels())
                )
                .containsOnly(
                    tuple("a", new HashMap<String, String>() {{
                        put("version", "a");
                    }}),
                    tuple("b", new HashMap<String, String>() {{
                        put("version", "b");
                    }})
                );
        });

        //when
        final Boolean deleteResult = istioClient.v1beta1DestinationRule().delete(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }

}
