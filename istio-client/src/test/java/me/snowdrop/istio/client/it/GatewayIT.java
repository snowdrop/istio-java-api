package me.snowdrop.istio.client.it;

import java.util.AbstractMap.SimpleEntry;

import me.snowdrop.istio.api.networking.v1beta1.Gateway;
import me.snowdrop.istio.api.networking.v1beta1.GatewayBuilder;
import me.snowdrop.istio.api.networking.v1beta1.GatewaySpec;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GatewayIT {

    private final IstioClient istioClient = new DefaultIstioClient();

    /*
  apiVersion: networking.istio.io/v1beta1
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
            .withApiVersion("networking.istio.io/v1beta1")
            .withNewMetadata()
            .withName("httpbin-gateway")
            .endMetadata()
            .withNewSpec()
            .addToSelector("istio", "ingressgateway")
            .addNewServer().withNewPort("http", 80, "HTTP", 80).withHosts("httpbin.example.com").endServer()
                .endSpec()
                .build();

        //when
        final Gateway resultResource = istioClient.v1beta1Gateway().create(gateway);

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
        final Boolean deleteResult = istioClient.v1beta1Gateway().delete(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }

}
