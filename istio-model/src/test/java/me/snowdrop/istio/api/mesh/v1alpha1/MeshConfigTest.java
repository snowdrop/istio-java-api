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
package me.snowdrop.istio.api.mesh.v1alpha1;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class MeshConfigTest extends BaseIstioTest {
    @Test
    public void checkDefaultMeshConfig() throws Exception {
        String configString = "# Set the following variable to true to disable policy checks by the Mixer.\n" +
                "# Note that metrics will still be reported to the Mixer.\n" +
                "disablePolicyChecks: false\n" +
                "\n" +
                "# Set enableTracing to false to disable request tracing.\n" +
                "enableTracing: true\n" +
                "\n" +
                "# Set accessLogFile to empty string to disable access log.\n" +
                "accessLogFile: \"/dev/stdout\"\n" +
                "#\n" +
                "# Deprecated: mixer is using EDS\n" +
                "mixerCheckServer: istio-policy.istio-system.svc.cluster.local:9091\n" +
                "mixerReportServer: istio-telemetry.istio-system.svc.cluster.local:9091\n" +
                "\n" +
                "# Unix Domain Socket through which envoy communicates with NodeAgent SDS to get\n" +
                "# key/cert for mTLS. Use secret-mount files instead of SDS if set to empty. \n" +
                "sdsUdsPath: \"\"\n" +
                "\n" +
                "# How frequently should Envoy fetch key/cert from NodeAgent.\n" +
                "sdsRefreshDelay: 15s\n" +
                "\n" +
                "#\n" +
                "defaultConfig:\n" +
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
                "  # The mode used to redirect inbound connections to Envoy. This setting\n" +
                "  # has no effect on outbound traffic: iptables REDIRECT is always used for\n" +
                "  # outbound connections.\n" +
                "  # If \"REDIRECT\", use iptables REDIRECT to NAT and redirect to Envoy.\n" +
                "  # The \"REDIRECT\" mode loses source addresses during redirection.\n" +
                "  # If \"TPROXY\", use iptables TPROXY to redirect to Envoy.\n" +
                "  # The \"TPROXY\" mode preserves both the source and destination IP\n" +
                "  # addresses and ports, so that they can be used for advanced filtering\n" +
                "  # and manipulation.\n" +
                "  # The \"TPROXY\" mode also configures the sidecar to run with the\n" +
                "  # CAP_NET_ADMIN capability, which is required to use TPROXY.\n" +
                "  #interceptionMode: REDIRECT\n" +
                "  #\n" +
                "  # Port where Envoy listens (on local host) for admin commands\n" +
                "  # You can exec into the istio-proxy container in a pod and\n" +
                "  # curl the admin port (curl http://localhost:15000/ ) to obtain\n" +
                "  # diagnostic information from Envoy. See\n" +
                "  # https://lyft.github.io/envoy/docs/operations/admin.html \n" +
                "  # for more details\n" +
                "  proxyAdminPort: 15000\n" +
                "  #\n" +
                "  # Zipkin trace collector\n" +
                "  zipkinAddress: zipkin.istio-system:9411\n" +
                "  #\n" +
                "  # Statsd metrics collector converts statsd metrics into Prometheus metrics.\n" +
                "  statsdUdpAddress: istio-statsd-prom-bridge.istio-system:9125\n" +
                "  #\n" +
                "  # Mutual TLS authentication between sidecars and istio control plane.\n" +
                "  controlPlaneAuthPolicy: NONE\n" +
                "  #\n" +
                "  # Address where istio Pilot service is running\n" +
                "  discoveryAddress: istio-pilot.istio-system:15007";
        MeshConfig meshConfig = mapper.readValue(configString, MeshConfig.class);
        assertNotNull(meshConfig);

        final ProxyConfig config = meshConfig.getDefaultConfig();
        assertEquals(10l, (long) config.getConnectTimeout().getSeconds());
        assertEquals(AuthenticationPolicy.NONE, config.getControlPlaneAuthPolicy());
        assertEquals("istio-pilot.istio-system:15007", config.getDiscoveryAddress());
        assertEquals("zipkin.istio-system:9411", config.getZipkinAddress());
        assertEquals("istio-statsd-prom-bridge.istio-system:9125", config.getStatsdUdpAddress());
    }

    @Test
    public void checkAuthMeshConfig() throws Exception {
        String configString = "authPolicy: MUTUAL_TLS\n" +
                "enableTracing: true\n" +
                "mixerAddress: istio-mixer.istio-system:15004\n" +
                "ingressService: istio-ingress\n" +
                "ingressControllerMode: DEFAULT\n" +
                "rdsRefreshDelay: 1s\n" +
                "defaultConfig:\n" +
                "  discoveryRefreshDelay: 1s\n" +
                "  connectTimeout: 10s\n" +
                "  configPath: \"/etc/istio/proxy\"\n" +
                "  binaryPath: \"/usr/local/bin/envoy\"\n" +
                "  serviceCluster: istio-proxy\n" +
                "  drainDuration: 45s\n" +
                "  parentShutdownDuration: 1m0s\n" +
                "  proxyAdminPort: 15000\n" +
                "  discoveryAddress: istio-pilot.istio-system:15003\n" +
                "  zipkinAddress: zipkin.istio-system:9411\n" +
                "  statsdUdpAddress: istio-mixer.istio-system:9125\n" +
                "  controlPlaneAuthPolicy: MUTUAL_TLS";
        MeshConfig meshConfig = mapper.readValue(configString, MeshConfig.class);
        assertNotNull(meshConfig);

        assertEquals(AuthenticationPolicy.MUTUAL_TLS, meshConfig.getAuthPolicy());
        assertEquals(IngressControllerMode.DEFAULT, meshConfig.getIngressControllerMode());
        assertEquals(AuthenticationPolicy.MUTUAL_TLS, meshConfig.getDefaultConfig().getControlPlaneAuthPolicy());

    }
}
