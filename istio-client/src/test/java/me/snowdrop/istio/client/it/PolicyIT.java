package me.snowdrop.istio.client.it;

import me.snowdrop.istio.api.authentication.v1alpha1.Policy;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicyBuilder;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicyList;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicySpec;
import me.snowdrop.istio.api.authentication.v1alpha1.TargetSelectorBuilder;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
                .withNewMetadata()
                .withName("basic-policy")
                .endMetadata()
                .withNewSpec()
                .addToTargets(new TargetSelectorBuilder().withName("service-a").build())
                .endSpec()
                .build();

        //when
        final Policy resultResource = istioClient.v1alpha1Policy().create(policy);

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


        final PolicyList list = istioClient.v1alpha1Policy().list();
        assertThat(list.getItems()).contains(resultResource);

        //when
        final Boolean deleteResult = istioClient.v1alpha1Policy().delete(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }

}
