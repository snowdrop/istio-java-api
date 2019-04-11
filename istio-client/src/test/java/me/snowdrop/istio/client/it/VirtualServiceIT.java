package me.snowdrop.istio.client.it;

import java.io.InputStream;
import java.util.List;

import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.networking.v1alpha3.HTTPRoute;
import me.snowdrop.istio.api.networking.v1alpha3.HttpStatusErrorType;
import me.snowdrop.istio.api.networking.v1alpha3.NumberPort;
import me.snowdrop.istio.api.networking.v1alpha3.PrefixMatchType;
import me.snowdrop.istio.api.networking.v1alpha3.VirtualService;
import me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceBuilder;
import me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceSpec;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class VirtualServiceIT {

    private final IstioClient istioClient = new DefaultIstioClient();

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
        final VirtualService virtualService = new VirtualServiceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("reviews-route").endMetadata()
                .withNewSpec()
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
                .endSpec()
                .build();

        //when
        final VirtualService resultResource = istioClient.virtualService().create(virtualService);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("VirtualService");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("reviews-route");
        });

        //and
        final VirtualServiceSpec resultVirtualServiceSpec = resultResource.getSpec();
        assertThat(resultVirtualServiceSpec).satisfies(vs -> {

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
            final Boolean deleteResult = istioClient.virtualService().delete(resultResource);

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
        final VirtualService virtualService = new VirtualServiceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("reviews-route2").endMetadata()
                .withNewSpec()
                .addToHosts(reviewsHost)
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(reviewsHost).withSubset("v2").withNewPort()
                .withNewNumberPort()
                .withNumber(9090).endNumberPort().endPort().endDestination()
                .endRoute()
                .endHttp()
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(reviewsHost).withSubset("v1").withNewPort()
                .withNewNumberPort()
                .withNumber(9090).endNumberPort().endPort().endDestination()
                .endRoute()
                .endHttp()
                .endSpec()
                .build();

        //when
        final VirtualService resultResource = istioClient.virtualService().create(virtualService);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("VirtualService");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("reviews-route2");
        });

        //and
        final VirtualServiceSpec resultSpec = resultResource.getSpec();

        //and
        assertThat(resultSpec).satisfies(vs -> {

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
            final Boolean deleteResult = istioClient.virtualService().delete(resultResource);

            //then
            assertThat(deleteResult).isTrue();
        });
    }

    /*
  apiVersion: networking.istio.io/v1alpha3
  kind: VirtualService
  metadata:
    name: ratings-route
  spec:
    hosts:
    - ratings.prod.svc.cluster.local
    http:
    - route:
      - destination:
          host: ratings.prod.svc.cluster.local
          subset: v1
      fault:
        abort:
          percent: 10
          httpStatus: 400
     */
    @Test
    public void checkVirtualServiceAbort() {
        final String ratingsHost = "ratings.prod.svc.cluster.local";
        final VirtualService virtualService = new VirtualServiceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("ratings-route").endMetadata()
                .withNewSpec()
                .addToHosts(ratingsHost)
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(ratingsHost).withSubset("v1")
                .endDestination()
                .endRoute()
                .withNewFault()
                .withNewAbort()
                .withPercent(10)
                .withNewHttpStatusErrorType(400)
                .endAbort()
                .endFault()
                .endHttp()
                .endSpec()
                .build();

        //when
        final VirtualService resultResource = istioClient.virtualService().create(virtualService);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("VirtualService");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("ratings-route");
        });

        //and
        final VirtualServiceSpec resultSpec = resultResource.getSpec();

        //and
        assertThat(resultSpec).satisfies(vs -> {

            assertThat(vs.getHosts()).containsExactly(ratingsHost);

            final List<HTTPRoute> httpList = vs.getHttp();
            assertThat(httpList).hasSize(1);

            //assert the host and subset were set correctly
            assertThat(httpList)
                    .flatExtracting("route")
                    .extracting("destination")
                    .extracting("host", "subset")
                    .containsOnly(
                            tuple(ratingsHost, "v1")
                    );

            //assert the fault was correctly set
            assertThat(httpList.get(0).getFault().getAbort()).satisfies(a -> {

                assertThat(a.getPercent()).isEqualTo(10);
                assertThat(a.getErrorType())
                        .isInstanceOfSatisfying(
                                HttpStatusErrorType.class,
                                e -> assertThat(e.getHttpStatus()).isEqualTo(400)
                        );
            });

            //when
            final Boolean deleteResult = istioClient.virtualService().delete(resultResource);

            //then
            assertThat(deleteResult).isTrue();
        });
    }

    @Test
    public void checkThatRegisterResourceWorksProperly() {
        final String ratingsHost = "ratings.prod.svc.cluster.local";
        final VirtualService virtualService = new VirtualServiceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("ratings-route").endMetadata()
                .withNewSpec()
                .addToHosts(ratingsHost)
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(ratingsHost).withSubset("v1")
                .endDestination()
                .endRoute()
                .withNewFault()
                .withNewAbort()
                .withPercent(10)
                .withNewHttpStatusErrorType(400)
                .endAbort()
                .endFault()
                .endHttp()
                .endSpec()
                .build();


        final IstioResource resource = istioClient.registerCustomResource(virtualService);
        assertThat(resource).isNotNull().satisfies(r -> assertThat(r.getSpec()).isInstanceOf(VirtualServiceSpec.class));
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("virtual-service.yaml");
        final List<IstioResource> resources = istioClient.registerCustomResources(inputStream);
        assertThat(resources).isNotNull().satisfies(list -> {
            assertThat(list.size()).isEqualTo(1);
            final IstioResource r = list.get(0);
            assertThat(r.getSpec()).isInstanceOf(VirtualServiceSpec.class);
        });
    }
}
