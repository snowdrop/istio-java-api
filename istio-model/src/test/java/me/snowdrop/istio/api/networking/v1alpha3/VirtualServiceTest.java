/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package me.snowdrop.istio.api.networking.v1alpha3;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.tests.BaseIstioTest;
import me.snowdrop.istio.util.YAML;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
                .withNewDestination().withHost(reviewsHost).withSubset("v2").withNewPort().withNewNumberPort()
                .withNewNumber(9090).endNumberPort().endPort().endDestination()
                .endRoute()
                .endHttp()
                .addNewHttp()
                .addNewRoute()
                .withNewDestination().withHost(reviewsHost).withSubset("v1").withNewPort().withNewNumberPort()
                .withNewNumber(9090).endNumberPort().endPort().endDestination()
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

        final Map<String, Integer> portSelector1 = (Map<String, Integer>) (destination.get("port"));
        assertNotNull(portSelector1);
        assertEquals(9090, portSelector1.get("number").intValue());

        http = https.get(1);
        destination = (Map) ((List<Map>) http.get("route")).get(0).get("destination");
        assertEquals(reviewsHost, destination.get("host"));
        assertEquals("v1", destination.get("subset"));

        final Map<String, Integer> portSelector2 = (Map<String, Integer>) (destination.get("port"));
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
                .addNewHttp()
                .addNewRoute().withNewDestination().withHost("details").withSubset("v1").endDestination().endRoute()
                .endHttp()
                .endVirtualServiceSpec()
                .build();

        final String output = mapper.writeValueAsString(virtualService);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(virtualService, reloaded);
    }

    @Test
    public void loadingFromYAMLShouldWork() {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("virtual-service.yaml");

        /*
        apiVersion: networking.istio.io/v1alpha3
 metadata:
 kind: VirtualService
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

        final VirtualService virtualService = YAML.loadIstioResource(inputStream, VirtualService.class);
        assertEquals("ratings.prod.svc.cluster.local", virtualService.getHosts().get(0));
        final List<HTTPRoute> http = virtualService.getHttp();
        assertEquals(1, http.size());
        final HTTPRoute route = http.get(0);
        final List<DestinationWeight> weights = route.getRoute();
        assertEquals(1, weights.size());
        final DestinationWeight weight = weights.get(0);
        assertEquals("ratings.prod.svc.cluster.local", weight.getDestination().getHost());
        assertEquals("v1", weight.getDestination().getSubset());
        assertNull(route.getFault().getDelay());
        final Abort abort = route.getFault().getAbort();
        assertEquals(10, abort.getPercent().intValue());
        assertEquals(400, ((HttpStatusErrorType) abort.getErrorType()).getHttpStatus().intValue());
    }
}
