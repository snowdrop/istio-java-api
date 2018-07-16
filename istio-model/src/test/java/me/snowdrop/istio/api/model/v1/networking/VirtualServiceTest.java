package me.snowdrop.istio.api.model.v1.networking;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioResourceBuilder;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VirtualServiceTest extends BaseIstioTest {
    /*
    ---
apiVersion: "networking.istio.io/v1alpha3"
kind: "VirtualService"
metadata:
  annotations: {}
  finalizers: []
  labels: {}
  name: "details"
  ownerReferences: []
spec:
  hosts:
  - "details"
  http:
  - route:
    - destination:
        host: "details"
        subset: "v1"
     */

    @Test
    public void checkBasicVirtualService() throws Exception {
        final IstioResource virtualService = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata()
                .withName("details")
                .endMetadata()
                .withNewVirtualServiceSpec()
                .withHosts("details")
                .addNewHttp()
                .addNewRoute()
                .withNewDestination()
                .withHost("details")
                .withSubset("v1")
                .endDestination()
                .endRoute()
                .endHttp()
                .endVirtualServiceSpec()
                .build();

        final String output = mapper.writeValueAsString(virtualService);

        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);

        assertEquals("VirtualService", reloaded.get("kind"));

        final Map metadata = reloaded.get("metadata");
        assertNotNull(metadata);
        assertEquals("details", metadata.get("name"));

        final Map<String, Map> spec = reloaded.get("spec");
        assertNotNull(spec);

        final List<Map> https = (List) spec.get("http");
        assertNotNull(https);

        final Map<String, Map> http = https.get(0);
        assertNotNull(http);

        final List<Map> routes = (List) http.get("route");
        assertNotNull(routes);

        final Map<String, Map> route = routes.get(0);
        assertNotNull(route);

        final Map<String, Map> destination = route.get("destination");
        assertNotNull(destination);

        assertEquals("details", destination.get("host"));
        assertEquals("v1", destination.get("subset"));
    }

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
    public void checkVirtualServiceWithMatch() throws IOException {
        final String reviewsHost = "reviews.prod.svc.cluster.local";
        final IstioResource resource = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("reviews-route").endMetadata()
                .withNewVirtualServiceSpec()
                .addToHosts(reviewsHost)
                .addNewHttp()
                .addNewMatch().withNewPrefixStringMatchUri("/wpcatalog").endMatch()
                .addNewMatch().withNewPrefixStringMatchUri("/consumercatalog").endMatch()
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

        final String output = mapper.writeValueAsString(resource);

        assertEquals(resource, mapper.readValue(output, IstioResource.class));

        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);


        assertEquals("VirtualService", reloaded.get("kind"));

        final Map metadata = reloaded.get("metadata");
        assertNotNull(metadata);
        assertEquals("reviews-route", metadata.get("name"));

        final Map<String, Map> spec = reloaded.get("spec");
        assertNotNull(spec);

        assertEquals(reviewsHost, ((List) spec.get("hosts")).get(0).toString());

        final List<Map> https = (List) spec.get("http");
        assertNotNull(https);

        Map<String, Map> http = https.get(0);
        assertNotNull(http);

        final List<Map> matches = (List) http.get("match");
        assertNotNull(matches);
        assertEquals(2, matches.size());
        assertEquals("/wpcatalog", ((Map) matches.get(0).get("uri")).get("prefix"));
        assertEquals("/consumercatalog", ((Map) matches.get(1).get("uri")).get("prefix"));

        assertEquals("/newcatalog", http.get("rewrite").get("uri"));

        Map destination = (Map) ((List<Map>) http.get("route")).get(0).get("destination");
        assertEquals(reviewsHost, destination.get("host"));
        assertEquals("v2", destination.get("subset"));

        http = https.get(1);
        destination = (Map) ((List<Map>) http.get("route")).get(0).get("destination");
        assertEquals(reviewsHost, destination.get("host"));
        assertEquals("v1", destination.get("subset"));
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
    public void checkVirtualServiceWithPortSelector() throws IOException {
        final String reviewsHost = "reviews.prod.svc.cluster.local";
        final IstioResource resource = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata().withName("reviews-route").endMetadata()
                .withNewVirtualServiceSpec()
                .addToHosts(reviewsHost)
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(reviewsHost).withSubset("v2").withPort(new PortSelector(9090)).endDestination()
                .endRoute()
                .endHttp()
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(reviewsHost).withSubset("v1").withPort(new PortSelector(9090)).endDestination()
                .endRoute()
                .endHttp()
                .endVirtualServiceSpec()
                .build();

        final String output = mapper.writeValueAsString(resource);

        assertEquals(resource, mapper.readValue(output, IstioResource.class));

        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);


        assertEquals("VirtualService", reloaded.get("kind"));

        final Map metadata = reloaded.get("metadata");
        assertNotNull(metadata);
        assertEquals("reviews-route", metadata.get("name"));

        final Map<String, Map> spec = reloaded.get("spec");
        assertNotNull(spec);

        assertEquals(reviewsHost, ((List) spec.get("hosts")).get(0).toString());

        final List<Map> https = (List) spec.get("http");
        assertNotNull(https);

        Map<String, Map> http = https.get(0);
        assertNotNull(http);


        Map destination = (Map) ((List<Map>) http.get("route")).get(0).get("destination");
        assertEquals(reviewsHost, destination.get("host"));
        assertEquals("v2", destination.get("subset"));

        final Map<String, Integer> portSelector1 = (Map<String, Integer>) ( destination.get("port") );
        assertNotNull(portSelector1);
        assertEquals(9090, portSelector1.get("number").intValue());

        http = https.get(1);
        destination = (Map) ((List<Map>) http.get("route")).get(0).get("destination");
        assertEquals(reviewsHost, destination.get("host"));
        assertEquals("v1", destination.get("subset"));

        final Map<String, Integer> portSelector2 = (Map<String, Integer>) ( destination.get("port") );
        assertNotNull(portSelector2);
        assertEquals(9090, portSelector2.get("number").intValue());
    }

    @Test
    public void roundtripBasicVirtualServiceShouldWork() throws Exception {
        final IstioResource virtualService = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata()
                .withName("details")
                .endMetadata()
                .withNewVirtualServiceSpec()
                .withHosts("details")
                .withHttp(new me.snowdrop.istio.api.model.v1.networking.HTTPRouteBuilder()
                        .withRoute(
                                new me.snowdrop.istio.api.model.v1.networking.DestinationWeightBuilder().withNewDestination()
                                        .withHost("details")
                                        .withSubset("v1")
                                        .endDestination()
                                        .build()
                        ).build()
                )
                .endVirtualServiceSpec()
                .build();

        final String output = mapper.writeValueAsString(virtualService);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(virtualService, reloaded);
    }
}
