/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package me.snowdrop.istio.api.networking.v1alpha3;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class DestinationRuleTest extends BaseIstioTest {
	@Test
	public void loadingFromYAMLIssue82ShouldWork() throws Exception {
		final InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("destination-rule-issue82.yaml");
		final DestinationRule destinationRule = mapper.readValue(inputStream, DestinationRule.class);

		final LoadBalancerSettings.LbPolicy policy = destinationRule.getSpec().getTrafficPolicy().getLoadBalancer().getLbPolicy();
		assertTrue(policy instanceof ConsistentHashLbPolicy);
		final ConsistentHashLbPolicy consistentHashLbPolicy = (ConsistentHashLbPolicy) policy;
		final ConsistentHashLB.HashKey hashKey = consistentHashLbPolicy.getConsistentHash().getHashKey();
		assertTrue(hashKey instanceof HttpCookieHashKey);
		final HttpCookieHashKey httpCookieHashKey = (HttpCookieHashKey) hashKey;
		assertEquals("user", httpCookieHashKey.getHttpCookie().getName());
	}
}
