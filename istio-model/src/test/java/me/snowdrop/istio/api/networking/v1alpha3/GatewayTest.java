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

import java.util.List;
import java.util.Map;

import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GatewayTest extends BaseIstioTest {
    /*
    ---
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: httpbin-gateway
spec:
  selector:
    istio: ingressgateway # use Istio default gateway implementation
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "httpbin.example.com"
     */

    @Test
    public void checkBasicGateway() throws Exception {
        final IstioResource gateway = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata()
                .withName("httpbin-gateway")
                .endMetadata()
                .withNewGatewaySpec()
                .addToSelector("istio", "ingressgateway")
                .addNewServer().withNewPort("http", 80, "HTTP").withHosts("httpbin.example.com").endServer()
                .endGatewaySpec()
                .build();

        final String output = mapper.writeValueAsString(gateway);
        Yaml parser = new Yaml();
        final Map<String, Map> reloaded = parser.loadAs(output, Map.class);

        assertEquals("Gateway", reloaded.get("kind"));

        final Map metadata = reloaded.get("metadata");
        assertNotNull(metadata);
        assertEquals("httpbin-gateway", metadata.get("name"));

        final Map<String, Map> spec = reloaded.get("spec");
        assertNotNull(spec);

        final Map<String, Map> selector = spec.get("selector");
        assertNotNull(selector);
        assertEquals("ingressgateway", selector.get("istio"));

        final List<Map> servers = (List) spec.get("servers");
        assertNotNull(servers);

        final Map<String, Map> server = servers.get(0);
        assertNotNull(server);

        final Map<String, Map> port = server.get("port");
        assertNotNull(port);
        assertEquals(80, port.get("number"));
        assertEquals("http", port.get("name"));
        assertEquals("HTTP", port.get("protocol"));

        final List<Map> hosts = (List) server.get("hosts");
        assertNotNull(hosts);
        assertEquals("httpbin.example.com", hosts.get(0));
    }

    @Test
    public void roundtripBasicGatewayShouldWork() throws Exception {
        final IstioResource gateway = new IstioResourceBuilder()
                .withApiVersion("networking.istio.io/v1alpha3")
                .withNewMetadata()
                .withName("httpbin-gateway")
                .endMetadata()
                .withNewGatewaySpec()
                .addToSelector("istio", "ingressgateway")
                .addNewServer().withNewPort("http", 80, "HTTP").withHosts("httpbin.example.com").endServer()
                .endGatewaySpec()
                .build();

        final String output = mapper.writeValueAsString(gateway);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(gateway, reloaded);
    }
}
