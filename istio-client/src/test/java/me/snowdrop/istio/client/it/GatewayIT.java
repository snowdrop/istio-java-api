package me.snowdrop.istio.client.it;

import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.networking.Gateway;
import me.snowdrop.istio.client.IstioClient;
import me.snowdrop.istio.client.KubernetesAdapter;
import org.junit.Test;

public class GatewayIT {

  private final IstioClient istioClient
      = new IstioClient(new KubernetesAdapter(new DefaultKubernetesClient()));

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
    final IstioResource gateway = new IstioResourceBuilder()
        .withApiVersion("networking.istio.io/v1alpha3")
        .withNewMetadata()
        .withName("httpbin-gateway")
        .endMetadata()
        .withNewGatewaySpec()
        .withSelector(new HashMap<String, String>() {{
          put("istio","ingressgateway");
        }})
        .withServers(new me.snowdrop.istio.api.networking.ServerBuilder()
            .withNewPort("http", 80, "HTTP")
            .withHosts("httpbin.example.com")
            .build() )
        .endGatewaySpec()
        .build();

    //when
    final IstioResource resultResource = istioClient.registerCustomResource(gateway);

    //then
    assertThat(resultResource).isNotNull().satisfies(istioResource -> {

      assertThat(istioResource.getKind()).isEqualTo("Gateway");

      assertThat(istioResource)
          .extracting("metadata")
          .extracting("name")
          .containsOnly("httpbin-gateway");
    });

    //and
    final IstioSpec resultSpec = resultResource.getSpec();
    assertThat(resultSpec).isNotNull().isInstanceOf(Gateway.class);

    //and
    final Gateway resultGateway = (Gateway) resultSpec;
    assertThat(resultGateway).satisfies(vs -> {

      assertThat(resultGateway.getSelector())
          .containsOnly(new SimpleEntry<>("istio", "ingressgateway"));

      assertThat(resultGateway.getServers()).hasSize(1);
      assertThat(resultGateway.getServers().get(0)).satisfies(server -> {

        assertThat(server.getHosts()).containsExactly("httpbin.example.com");
        assertThat(server.getPort()).isNotNull().satisfies(port -> {

          assertThat(port.getName()).isEqualTo("http");
          assertThat(port.getProtocol()).isEqualTo("HTTP");
          assertThat(port.getNumber()).isEqualTo(80);
        });
      });

    });

    //when
    final Boolean deleteResult = istioClient.unregisterCustomResource(resultResource);

    //then
    assertThat(deleteResult).isTrue();
  }

}
