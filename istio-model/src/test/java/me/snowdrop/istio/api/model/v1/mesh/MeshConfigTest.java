/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mesh;

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
