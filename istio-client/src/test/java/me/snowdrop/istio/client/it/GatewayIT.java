package me.snowdrop.istio.client.it;

import java.util.AbstractMap.SimpleEntry;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.networking.v1alpha3.Gateway;
import me.snowdrop.istio.api.networking.v1alpha3.GatewayBuilder;
import me.snowdrop.istio.api.networking.v1alpha3.GatewaySpec;
import me.snowdrop.istio.clientv2.IstioClient;
import me.snowdrop.istio.clientv2.DefaultIstioClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GatewayIT {

    private final IstioClient istioClient = new DefaultIstioClient();

    /*
  apiVersion: networking.istio.io/v1alpha3
  kind: Gateway
  metadata:
  name: httpbin-gateway
  spec:
  selector:
    istio: ingressgateway # use Istio default gateway implementation
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "httpbin.example.com"
     */
    @Test
    public void checkBasicGateway() {
        //given
        final Gateway gateway = new GatewayBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata()
                .withName("httpbin-gateway")
                .endMetadata()
                .withNewSpec()
                .addToSelector("istio", "ingressgateway")
                .addNewServer().withNewPort("http", 80, "HTTP").withHosts("httpbin.example.com").endServer()
                .endSpec()
                .build();

        //when
        final Gateway resultResource = istioClient.gateway().create(gateway);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("Gateway");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("httpbin-gateway");
        });

        //and
        final GatewaySpec resultSpec = resultResource.getSpec();

        //and
        assertThat(resultSpec).satisfies(gs -> {

            assertThat(gs.getSelector())
                    .containsOnly(new SimpleEntry<>("istio", "ingressgateway"));

            assertThat(gs.getServers()).hasSize(1);
            assertThat(gs.getServers().get(0)).satisfies(server -> {

                assertThat(server.getHosts()).containsExactly("httpbin.example.com");
                assertThat(server.getPort()).isNotNull().satisfies(port -> {

                    assertThat(port.getName()).isEqualTo("http");
                    assertThat(port.getProtocol()).isEqualTo("HTTP");
                    assertThat(port.getNumber()).isEqualTo(80);
                });
            });

        });

        //when
        final Boolean deleteResult = istioClient.gateway().delete(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }

}
