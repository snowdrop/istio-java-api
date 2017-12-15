/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.routing;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.snowdrop.istio.api.model.v1.mesh.*;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class RouteRuleTest {

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
        YAMLMapper mapper = new YAMLMapper();
        RouteRule routeRule = new RouteRuleBuilder()
                .withNewDestination()
                .withName("reviews")
                .withNamespace("my-namespace")
                .endDestination()
                .addNewRoute()
                .addToLabels("version", "v1")
                .withWeight(100)
                .endRoute()
                .build();

        final String output = mapper.writeValueAsString(routeRule);
        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);

        final Map<String, Map> destination = reloaded.get("destination");
        assertNotNull(destination);
        assertEquals("reviews", destination.get("name"));
        assertEquals("my-namespace", destination.get("namespace"));

        final List<Map> routes = (List) reloaded.get("route");
        assertNotNull(routes);
        final Map route = routes.get(0);
        assertNotNull(route);
        assertEquals(100, route.get("weight"));
        assertEquals("v1", ((Map) route.get("labels")).get("version"));

        String configString = "# Uncomment the following line to enable mutual TLS between proxies\n" +
                "# authPolicy: MUTUAL_TLS\n" +
                "#\n" +
                "# Edit this list to avoid using mTLS to connect to these services.\n" +
                "# Typically, these are control services (e.g kubernetes API server) that don't have Istio sidecar\n" +
                "# to transparently terminate mTLS authentication.\n" +
                "mtlsExcludedServices: [\"kubernetes.default.svc.cluster.local\"]\n" +
                "\n" +
                "# Set the following variable to true to disable policy checks by the Mixer.\n" +
                "# Note that metrics will still be reported to the Mixer.\n" +
                "disablePolicyChecks: false\n" +
                "# Set enableTracing to false to disable request tracing.\n" +
                "enableTracing: true\n" +
                "#\n" +
                "# To disable the mixer completely (including metrics), comment out\n" +
                "# the following line\n" +
                "mixerAddress: istio-mixer.istio-system:15004\n" +
                "# This is the ingress service name, update if you used a different name\n" +
                "ingressService: istio-ingress\n" +
                "#\n" +
                "# Along with discoveryRefreshDelay, this setting determines how\n" +
                "# frequently should Envoy fetch and update its internal configuration\n" +
                "# from Istio Pilot. Lower refresh delay results in higher CPU\n" +
                "# utilization and potential performance loss in exchange for faster\n" +
                "# convergence. Tweak this value according to your setup.\n" +
                "rdsRefreshDelay: 1s\n" +
                "#\n" +
                "defaultConfig:\n" +
                "  # NOTE: If you change any values in this section, make sure to make\n" +
                "  # the same changes in start up args in istio-ingress pods.\n" +
                "  # See rdsRefreshDelay for explanation about this setting.\n" +
                "  discoveryRefreshDelay: 1s\n" +
                "  #\n" +
                "  # TCP connection timeout between Envoy & the application, and between Envoys.\n" +
                "  connectTimeout: 10s\n" +
                "  #\n" +
                "  ### ADVANCED SETTINGS #############\n" +
                "  # Where should envoy's configuration be stored in the istio-proxy container\n" +
                "  configPath: \"/etc/istio/proxy\"\n" +
                "  binaryPath: \"/usr/local/bin/envoy\"\n" +
                "  # The pseudo service name used for Envoy.\n" +
                "  serviceCluster: istio-proxy\n" +
                "  # These settings that determine how long an old Envoy\n" +
                "  # process should be kept alive after an occasional reload.\n" +
                "  drainDuration: 45s\n" +
                "  parentShutdownDuration: 1m0s\n" +
                "  #\n" +
                "  # Port where Envoy listens (on local host) for admin commands\n" +
                "  # You can exec into the istio-proxy container in a pod and\n" +
                "  # curl the admin port (curl http://localhost:15000/) to obtain\n" +
                "  # diagnostic information from Envoy. See\n" +
                "  # https://lyft.github.io/envoy/docs/operations/admin.html\n" +
                "  # for more details\n" +
                "  proxyAdminPort: 15000\n" +
                "  #\n" +
                "  # Address where Istio Pilot service is running\n" +
                "  discoveryAddress: istio-pilot.istio-system:15003\n" +
                "  #\n" +
                "  # Zipkin trace collector\n" +
                "  zipkinAddress: zipkin.istio-system:9411\n" +
                "  #\n" +
                "  # Statsd metrics collector. Istio mixer exposes a UDP endpoint\n" +
                "  # to collect and convert statsd metrics into Prometheus metrics.\n" +
                "  statsdUdpAddress: istio-mixer.istio-system:9125\n" +
                "  # Uncomment the following line to enable mutual TLS authentication between\n" +
                "  # sidecars and istio control plane.\n" +
                "  # controlPlaneAuthPolicy: MUTUAL_TLS";
        MeshConfig meshConfig = mapper.readValue(configString, MeshConfig.class);
        assertNotNull(meshConfig);

        final ProxyConfig config = meshConfig.getDefaultConfig();
        assertEquals(10l, (long) config.getConnectTimeout().getSeconds());
        assertNull(config.getControlPlaneAuthPolicy());
        assertEquals("istio-pilot.istio-system:15003", config.getDiscoveryAddress());
        assertEquals("zipkin.istio-system:9411", config.getZipkinAddress());
        assertEquals("istio-mixer.istio-system:9125", config.getStatsdUdpAddress());
    }
}
