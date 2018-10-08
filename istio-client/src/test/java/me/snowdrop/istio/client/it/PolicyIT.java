package me.snowdrop.istio.client.it;

import static org.assertj.core.api.Assertions.assertThat;

import me.snowdrop.istio.api.authentication.v1alpha1.Policy;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicyBuilder;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicySpec;
import me.snowdrop.istio.api.authentication.v1alpha1.TargetSelectorBuilder;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.junit.Test;

public class PolicyIT {

    private final IstioClient istioClient = new DefaultIstioClient();

    /*
    apiVersion: authentication.istio.io/v1alpha1
    kind: Policy
    metadata:
      name: basic-policy
    spec:
      targets:
      - name: service-a
     */
    @Test
    public void checkBasicPolicy() {
        //given
        final Policy policy = new PolicyBuilder()
                .withApiVersion("authentication.istio.io/v1alpha1")
                .withNewMetadata()
                .withName("basic-policy")
                .endMetadata()
                .withNewSpec()
                .addToTargets(new TargetSelectorBuilder().withName("service-a").build())
                .endSpec()
                .build();

        //when
        final Policy resultResource = istioClient.policy().create(policy);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("Policy");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("basic-policy");
        });

        //and
        final PolicySpec resultSpec = resultResource.getSpec();

        //and
        assertThat(resultSpec).satisfies(ps -> {

            assertThat(ps.getTargets()).hasSize(1);
            assertThat(ps.getTargets().get(0)).satisfies(ts ->
                assertThat(ts.getName()).isEqualTo("service-a")
            );
        });

        //when
        final Boolean deleteResult = istioClient.policy().delete(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }

}
