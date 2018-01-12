/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.routing;

import java.util.List;
import java.util.Map;

import me.snowdrop.istio.api.model.IstioBaseResource;
import me.snowdrop.istio.api.model.IstioBaseResourceBuilder;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class RouteRuleTest extends BaseIstioTest {

    /*
apiVersion: config.istio.io/v1alpha2
kind: RouteRule
metadata:
  name: my-rule
  namespace: default # optional (default is "default")
spec:
  destination:
    name: reviews
    namespace: my-namespace # optional (default is metadata namespace field)
  route:
  - labels:
      version: v1
    weight: 100
     */
    @Test
    public void checkBasicRoute() throws Exception {
        final IstioBaseResource routeRule = new IstioBaseResourceBuilder()
                .withNewMetadata()
                .withName("my-rule")
                .endMetadata()
                .withNewRouteRuleSpec()
                .withNewDestination()
                .withName("reviews")
                .withNamespace("my-namespace")
                .endDestination()
                .addNewRoute()
                .addToLabels("version", "v1")
                .withWeight(100)
                .endRoute()
                .endRouteRuleSpec()
                .build();

        final String output = mapper.writeValueAsString(routeRule);
        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);

        assertEquals("RouteRule", reloaded.get("kind"));

        final Map metadata = reloaded.get("metadata");
        assertNotNull(metadata);
        assertEquals("my-rule", metadata.get("name"));

        final Map<String, Map> spec = reloaded.get("spec");
        assertNotNull(spec);

        final Map<String, Map> destination = spec.get("destination");
        assertNotNull(destination);
        assertEquals("reviews", destination.get("name"));
        assertEquals("my-namespace", destination.get("namespace"));

        final List<Map> routes = (List) spec.get("route");
        assertNotNull(routes);
        final Map route = routes.get(0);
        assertNotNull(route);
        assertEquals(100, route.get("weight"));
        assertEquals("v1", ((Map) route.get("labels")).get("version"));
    }

    @Test
    public void roundtripBasicRouteShouldWork() throws Exception {
        final IstioBaseResource routeRule = new IstioBaseResourceBuilder()
                .withNewMetadata()
                .withGenerateName("my-rule")
                .endMetadata()
                .withNewRouteRuleSpec()
                .withNewDestination()
                .withName("reviews")
                .withNamespace("namespace")
                .endDestination()
                .addNewRoute()
                .withWeight(100)
                .addToLabels("version", "v1")
                .endRoute()
                .endRouteRuleSpec()
                .build();

        final String output = mapper.writeValueAsString(routeRule);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(routeRule, reloaded);
    }
}
