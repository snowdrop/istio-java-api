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
import static org.junit.Assert.assertNull;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class MeshConfigTest extends BaseIstioTest {
    @Test
    public void checkDefaultMeshConfig() throws Exception {
        String configString = "mtlsExcludedServices: [\"kubernetes.default.svc.cluster.local\"]\n" +
                "disablePolicyChecks: false\n" +
                "enableTracing: true\n" +
                "mixerAddress: istio-mixer.istio-system:15004\n" +
                "ingressService: istio-ingress\n" +
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
                "  statsdUdpAddress: istio-mixer.istio-system:9125\n";
        MeshConfig meshConfig = mapper.readValue(configString, MeshConfig.class);
        assertNotNull(meshConfig);

        final ProxyConfig config = meshConfig.getDefaultConfig();
        assertEquals(10l, (long) config.getConnectTimeout().getSeconds());
        assertNull(config.getControlPlaneAuthPolicy());
        assertEquals("istio-pilot.istio-system:15003", config.getDiscoveryAddress());
        assertEquals("zipkin.istio-system:9411", config.getZipkinAddress());
        assertEquals("istio-mixer.istio-system:9125", config.getStatsdUdpAddress());
    }

    @Test
    public void checkAuthMeshConfig() throws Exception {
        String configString = "authPolicy: MUTUAL_TLS\n" +
                "mtlsExcludedServices: [\"kubernetes.default.svc.cluster.local\"]\n" +
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
