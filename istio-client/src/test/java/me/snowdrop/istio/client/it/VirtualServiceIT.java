package me.snowdrop.istio.client.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import java.util.List;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.networking.HTTPRoute;
import me.snowdrop.istio.api.networking.NumberPort;
import me.snowdrop.istio.api.networking.PrefixMatchType;
import me.snowdrop.istio.api.networking.VirtualService;
import me.snowdrop.istio.client.IstioClient;
import me.snowdrop.istio.client.KubernetesAdapter;
import org.junit.Test;

public class VirtualServiceIT {

  private final IstioClient istioClient
      = new IstioClient(new KubernetesAdapter(new DefaultKubernetesClient()));


  /*
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
name: reviews-route
spec:
hosts:
- reviews.prod.svc.cluster.local
http:
- match:
  - uri:
      prefix: "/wpcatalog"
  - uri:
      prefix: "/consumercatalog"
  rewrite:
    uri: "/newcatalog"
  route:
  - destination:
      host: reviews.prod.svc.cluster.local
      subset: v2
- route:
  - destination:
      host: reviews.prod.svc.cluster.local
      subset: v1
   */
  @Test
  public void checkVirtualServiceWithMatch() {
    //given
    final String reviewsHost = "reviews.prod.svc.cluster.local";
    final IstioResource virtualService = new IstioResourceBuilder()
        .withApiVersion("networking.istio.io/v1alpha3")
        .withNewMetadata().withName("reviews-route").endMetadata()
        .withNewVirtualServiceSpec()
        .addToHosts(reviewsHost)
        .addNewHttp()
        .addNewMatch().withNewUri().withNewPrefixMatchType("/wpcatalog").endUri().endMatch()
        .addNewMatch().withNewUri().withNewPrefixMatchType("/consumercatalog").endUri().endMatch()
        .withNewRewrite().withUri("/newcatalog").endRewrite()
        .addNewRoute()
        .withNewDestination().withHost(reviewsHost).withSubset("v2").endDestination()
        .endRoute()
        .endHttp()
        .addNewHttp()
        .addNewRoute()
        .withNewDestination().withHost(reviewsHost).withSubset("v1").endDestination()
        .endRoute()
        .endHttp()
        .endVirtualServiceSpec()
        .build();

    //when
    final IstioResource resultResource = istioClient.registerCustomResource(virtualService);

    //then
    assertThat(resultResource).isNotNull().satisfies(istioResource -> {

      assertThat(istioResource.getKind()).isEqualTo("VirtualService");

      assertThat(istioResource)
          .extracting("metadata")
          .extracting("name")
          .containsOnly("reviews-route");
    });

    //and
    final IstioSpec resultSpec = resultResource.getSpec();
    assertThat(resultSpec).isNotNull().isInstanceOf(VirtualService.class);

    //and
    final VirtualService resultVirtualService = (VirtualService) resultSpec;
    assertThat(resultVirtualService).satisfies(vs -> {

      assertThat(vs.getHosts()).containsExactly(reviewsHost);
      assertThat(vs.getGateways()).isEmpty();

      final List<HTTPRoute> httpList = vs.getHttp();
      assertThat(httpList).hasSize(2);
      assertThat(httpList.get(0)).satisfies(http -> {

        assertThat(http)
            .extracting("rewrite")
            .extracting("uri")
            .containsOnly("/newcatalog");

        assertThat(http.getMatch())
            .extracting("uri")
            .extracting("matchType")
            .extracting("class", "prefix")
            .containsOnly(
                tuple(PrefixMatchType.class, "/wpcatalog"),
                tuple(PrefixMatchType.class, "/consumercatalog")
            );

        assertThat(http.getRoute())
            .hasSize(1)
            .extracting("destination")
            .extracting("host", "subset")
            .containsOnly(tuple("reviews.prod.svc.cluster.local", "v2"));
      });

      assertThat(httpList.get(1)).satisfies(http -> {

        assertThat(http.getRoute())
            .hasSize(1)
            .extracting("destination")
            .extracting("host", "subset")
            .containsOnly(tuple("reviews.prod.svc.cluster.local", "v1"));
      });

      //when
      final Boolean deleteResult = istioClient.unregisterCustomResource(resultResource);

      //then
      assertThat(deleteResult).isTrue();
    });
  }

  /*
apiVersion: "networking.istio.io/v1alpha3"
kind: "VirtualService"
metadata:
name: "reviews-route"
spec:
hosts:
- "reviews.prod.svc.cluster.local"
http:
- route:
  - destination:
      host: "reviews.prod.svc.cluster.local"
      port:
        number: 9090
      subset: "v2"
- route:
  - destination:
      host: "reviews.prod.svc.cluster.local"
      port:
        number: 9090
      subset: "v1"
  */
  @Test
  public void checkVirtualServiceWithPortSelector() {
    final String reviewsHost = "reviews.prod.svc.cluster.local";
    final IstioResource virtualService = new IstioResourceBuilder()
        .withApiVersion("networking.istio.io/v1alpha3")
        .withNewMetadata().withName("reviews-route2").endMetadata()
        .withNewVirtualServiceSpec()
        .addToHosts(reviewsHost)
        .addNewHttp()
        .addNewRoute()
        .withNewDestination().withHost(reviewsHost).withSubset("v2").withNewPort()
        .withNewNumberPort()
        .withNewNumber(9090).endNumberPort().endPort().endDestination()
        .endRoute()
        .endHttp()
        .addNewHttp()
        .addNewRoute()
        .withNewDestination().withHost(reviewsHost).withSubset("v1").withNewPort()
        .withNewNumberPort()
        .withNewNumber(9090).endNumberPort().endPort().endDestination()
        .endRoute()
        .endHttp()
        .endVirtualServiceSpec()
        .build();

    //when
    final IstioResource resultResource = istioClient.registerCustomResource(virtualService);

    //then
    assertThat(resultResource).isNotNull().satisfies(istioResource -> {

      assertThat(istioResource.getKind()).isEqualTo("VirtualService");

      assertThat(istioResource)
          .extracting("metadata")
          .extracting("name")
          .containsOnly("reviews-route2");
    });

    //and
    final IstioSpec resultSpec = resultResource.getSpec();
    assertThat(resultSpec).isNotNull().isInstanceOf(VirtualService.class);

    //and
    final VirtualService resultVirtualService = (VirtualService) resultSpec;
    assertThat(resultVirtualService).satisfies(vs -> {

      assertThat(vs.getHosts()).containsExactly(reviewsHost);
      assertThat(vs.getGateways()).isEmpty();

      final List<HTTPRoute> httpList = vs.getHttp();
      assertThat(httpList).hasSize(2);

      //assert the host and subset were set correctly
      assertThat(httpList)
          .flatExtracting("route")
          .extracting("destination")
          .extracting("host", "subset")
          .containsOnly(
              tuple("reviews.prod.svc.cluster.local", "v1"),
              tuple("reviews.prod.svc.cluster.local", "v2")
          );

      //assert the port was correctly
      assertThat(httpList)
          .flatExtracting("route")
          .extracting("destination")
          .extracting("port")
          .extracting("port")
          .extracting("class", "number")
          .containsOnly(
              tuple(NumberPort.class, 9090),
              tuple(NumberPort.class, 9090)
          );

      //when
      final Boolean deleteResult = istioClient.unregisterCustomResource(resultResource);

      //then
      assertThat(deleteResult).isTrue();
    });
  }
}
