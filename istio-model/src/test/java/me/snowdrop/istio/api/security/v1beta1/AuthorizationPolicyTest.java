/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.istio.api.security.v1beta1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class AuthorizationPolicyTest extends BaseIstioTest {
    @Test
    public void roundtripShouldWork() throws JsonProcessingException {
        /*
        apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
 name: httpbin
 namespace: foo
spec:
 action: ALLOW
 rules:
 - from:
   - source:
       principals: ["cluster.local/ns/default/sa/sleep"]
   - source:
       namespaces: ["test"]
   to:
   - operation:
       methods: ["GET"]
       paths: ["/info*"]
   - operation:
       methods: ["POST"]
       paths: ["/data"]
   when:
   - key: request.auth.claims[iss]
     values: ["https://accounts.google.com"]
         */
        final AuthorizationPolicy policy = new AuthorizationPolicyBuilder()
            .withNewMetadata().withName("httpbin").withNamespace("foo").endMetadata()
            .withNewSpec()
            .withAction(Action.ALLOW)
            .addNewRule()
            .addNewFrom()
            .withNewSource().addNewPrincipal("cluster.local/ns/default/sa/sleep").endSource()
            .withNewSource().addNewNamespace("test").endSource()
            .endFrom()
            .addNewTo().withNewOperation().addNewMethod("GET").addNewPath("/info*").endOperation().endTo()
            .addNewTo().withNewOperation().addNewMethod("POST").addNewPath("/data").endOperation().endTo()
            .addNewWhen().withKey("request.auth.claims[iss]").addNewValue("https://accounts.google.com").endWhen()
            .endRule()
            .endSpec()
            .build();
        
        final String output = mapper.writeValueAsString(policy);
        
        HasMetadata reloaded = mapper.readValue(output, HasMetadata.class);
        
        assertEquals(policy, reloaded);
    }
}
