package me.snowdrop.istio.client.it;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.HasMetadata;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.networking.v1beta1.ExactMatchType;
import me.snowdrop.istio.api.networking.v1beta1.HTTPMatchRequest;
import me.snowdrop.istio.api.networking.v1beta1.HTTPMatchRequestBuilder;
import me.snowdrop.istio.api.networking.v1beta1.HTTPRoute;
import me.snowdrop.istio.api.networking.v1beta1.HttpStatusErrorType;
import me.snowdrop.istio.api.networking.v1beta1.PrefixMatchType;
import me.snowdrop.istio.api.networking.v1beta1.StringMatch;
import me.snowdrop.istio.api.networking.v1beta1.VirtualService;
import me.snowdrop.istio.api.networking.v1beta1.VirtualServiceBuilder;
import me.snowdrop.istio.api.networking.v1beta1.VirtualServiceSpec;
import me.snowdrop.istio.client.DefaultIstioClient;
import me.snowdrop.istio.client.IstioClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class VirtualServiceIT {
    
    private final IstioClient istioClient = new DefaultIstioClient();
    
    /*
  apiVersion: networking.istio.io/v1beta1
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
            .withApiVersion("networking.istio.io/v1beta1")
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
        final VirtualService resultResource = istioClient.v1beta1VirtualService().create(virtualService);
    
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
            final Boolean deleteResult = istioClient.v1beta1VirtualService().delete(resultResource);
    
            //then
            assertThat(deleteResult).isTrue();
        });
    }
    
    /*
  apiVersion: "networking.istio.io/v1beta1"
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
            .withApiVersion("networking.istio.io/v1beta1")
            .withNewMetadata().withName("reviews-route2").endMetadata()
            .withNewSpec()
            .addToHosts(reviewsHost)
            .addNewHttp()
            .addNewRoute()
            .withNewDestination().withHost(reviewsHost).withSubset("v2").withNewPort()
            .withNumber(9090).endPort().endDestination()
            .endRoute()
            .endHttp()
            .addNewHttp()
            .addNewRoute()
            .withNewDestination().withHost(reviewsHost).withSubset("v1").withNewPort()
            .withNumber(9090).endPort().endDestination()
            .endRoute()
            .endHttp()
            .endSpec()
            .build();
        
        //when
        final VirtualService resultResource = istioClient.v1beta1VirtualService().create(virtualService);
    
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
                    tuple(Integer.class, 9090),
                    tuple(Integer.class, 9090)
                );
            
            //when
            final Boolean deleteResult = istioClient.v1beta1VirtualService().delete(resultResource);
    
            //then
            assertThat(deleteResult).isTrue();
        });
    }
    
    /*
  apiVersion: networking.istio.io/v1beta1
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
        final VirtualService resultResource = istioClient.v1beta1VirtualService()
            .createNew()
            .withNewMetadata().withName("ratings-route").endMetadata()
            .withNewSpec()
            .addNewHost(ratingsHost)
            .addNewHttp()
            .withNewFault()
            .withNewAbort()
            .withNewPercentage(10.).withNewHttpStatusErrorType(400)
            .endAbort()
            .endFault()
            .addNewRoute()
            .withNewDestination().withHost(ratingsHost).withSubset("v1").endDestination()
            .endRoute()
            .endHttp()
            .endSpec().done();
    
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
    
                assertThat(a.getPercentage().getValue()).isEqualTo(10.);
                assertThat(a.getErrorType())
                    .isInstanceOfSatisfying(
                        HttpStatusErrorType.class,
                        e -> assertThat(e.getHttpStatus()).isEqualTo(400)
                    );
            });
    
            //when
            final Boolean deleteResult = istioClient.v1beta1VirtualService().delete(resultResource);
    
            //then
            assertThat(deleteResult).isTrue();
        });
    }
    
    @Test
    public void checkThatRegisterResourceWorksProperly() {
        IstioResource resource = null;
        try {
            final String ratingsHost = "ratings.prod.svc.cluster.local";
            final VirtualService virtualService = new VirtualServiceBuilder()
                .withApiVersion("networking.istio.io/v1beta1")
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
                .withNewPercentage()
                    .withValue(10.0)
                    .endPercentage()
                .withNewHttpStatusErrorType(400)
                .endAbort()
                .endFault()
                .endHttp()
                .endSpec()
                .build();
            
            
            resource = istioClient.registerCustomResource(virtualService);
            assertThat(resource).isNotNull().satisfies(r -> assertThat(r.getSpec()).isInstanceOf(VirtualServiceSpec.class));
        } finally {
            if (resource != null) {
                istioClient.v1beta1VirtualService().delete((VirtualService) resource);
            }
        }
        
        List<IstioResource> resources = null;
        try {
            final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("virtual-service.yaml");
            resources = istioClient.registerCustomResources(inputStream);
            assertThat(resources).isNotNull().satisfies(list -> {
                assertThat(list.size()).isEqualTo(1);
                final IstioResource r = list.get(0);
                assertThat(r.getSpec()).isInstanceOf(VirtualServiceSpec.class);
            });
        } finally {
            if (resources != null) {
                istioClient.v1beta1VirtualService().delete((VirtualService) resources.get(0));
            }
        }
    }
    
    @Test
    public void checkThatEditingMatchWorksProperly() {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("virtual-service-with-matches.yaml");
        IstioClient client = new DefaultIstioClient();
        List<HasMetadata> result = client.load(inputStream).createOrReplace();
        
        VirtualService done = null;
        try {
            assertThat(result).isNotEmpty();
            assertThat(result.size()).isEqualTo(1);
            final HasMetadata hasMetadata = result.get(0);
            assertThat(hasMetadata).isInstanceOf(VirtualService.class);
            
            Map<String, StringMatch> matchMap = new HashMap<>();
            matchMap.put("tenantid", new StringMatch(new ExactMatchType("coke")));
            HTTPMatchRequest req = new HTTPMatchRequestBuilder().withHeaders(matchMap)
                .build();
            
            done = istioClient.v1beta1VirtualService().withName("reviews-route")
                .edit().editSpec()
                .removeMatchingFromHttp(h -> h.hasMatchingMatch(m -> m.hasHeaders() && m.getHeaders().equals(matchMap)) && h.hasMatchingRoute(r -> r.buildDestination().getHost().equals("service-coke")))
                .endSpec().done();
            
            assertThat(done.getSpec().getHttp())
                .flatExtracting(HTTPRoute::getMatch)
                .doesNotContain(req);
        } finally {
            if (done != null) {
                istioClient.v1beta1VirtualService().delete(done);
            }
        }
    }
}
