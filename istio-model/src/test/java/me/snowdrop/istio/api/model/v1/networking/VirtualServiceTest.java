package me.snowdrop.istio.api.model.v1.networking;

import me.snowdrop.istio.tests.BaseIstioTest;

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
