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

import io.fabric8.kubernetes.api.model.HasMetadata;
import me.snowdrop.istio.api.Duration;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class VirtualServiceTest extends BaseIstioTest {


	@Test
	public void roundtripBasicVirtualServiceShouldWork() throws Exception {
		Duration fixedDelay = new Duration(0, 1L);
		FixedDelayHttpDelayType fDelayHttpDelayType = new FixedDelayHttpDelayType(fixedDelay);
		final String apiVersion = "networking.istio.io/v1alpha3";
		final VirtualService virtualService = new VirtualServiceBuilder().withApiVersion(apiVersion)
				.withNewMetadata().withName("vs_name").withNamespace("ns").endMetadata()
				.withNewSpec().withHosts("svc_name")
				.addNewHttp()
				.withNewFault()
				.withNewDelay()
				.withFixedDelayHttpType(fDelayHttpDelayType)
				.withNewPercentage(10D)
				.endDelay()
				.endFault()
				.endHttp()
				.endSpec()
				.build();
		final String output = mapper.writeValueAsString(virtualService);

		HasMetadata reloaded = mapper.readValue(output, HasMetadata.class);

		assertEquals(virtualService, reloaded);
		assertEquals(apiVersion, reloaded.getApiVersion());
	}


	@Test
	public void loadingFromYAMLIssue103ShouldWork() throws Exception {
		final InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("virtual-service-issue103.yaml");
		final VirtualService virtualService = mapper.readValue(inputStream, VirtualService.class);

        /*
        ...
        spec:
  http:
    - fault:
        delay:
          fixedDelay: 6s
          percentage:
            value: 90.0

              ...
         */
		final Percent percentage = virtualService.getSpec().getHttp().get(0).getFault().getDelay().getPercentage();
		assertEquals(90.0, percentage.getValue(), 0);
	}
}
