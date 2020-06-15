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
package me.snowdrop.istio.api.networking.v1beta1;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class CorsPolicyTest extends BaseIstioTest {
	@Test
	public void roundtripShouldWork() throws Exception {
		final String prefix = "example.com";
		final String allowOrigin = "bar.com";
		final CorsPolicy policy = new CorsPolicyBuilder()
				.addNewAllowOrigin()
				.withNewPrefixMatchType(prefix)
				.endAllowOrigin()
				.addNewDeprecatedAllowOrigin(allowOrigin)
				.build();

		final String output = mapper.writeValueAsString(policy);

		CorsPolicy reloaded = mapper.readValue(output, CorsPolicy.class);

		assertEquals(1, reloaded.getAllowOrigins().size());
		final StringMatch match = reloaded.getAllowOrigins().get(0);
		final StringMatch.MatchType matchType = match.getMatchType();
		if (matchType instanceof PrefixMatchType) {
			PrefixMatchType prefixMatchType = (PrefixMatchType) matchType;
			assertEquals(prefix, prefixMatchType.getPrefix());
		} else {
			fail();
		}

		assertEquals(1, reloaded.getDeprecatedAllowOrigin().size());
		assertEquals(allowOrigin, reloaded.getDeprecatedAllowOrigin().get(0));

		assertEquals(policy, reloaded);
	}
}
