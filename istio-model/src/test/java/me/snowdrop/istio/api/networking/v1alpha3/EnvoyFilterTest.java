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
import me.snowdrop.istio.util.StructHelper;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import static me.snowdrop.istio.api.networking.v1alpha3.PatchContext.SIDECAR_OUTBOUND;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class EnvoyFilterTest extends BaseIstioTest {

	@Test
	public void loadingFromYAMLIssue99ShouldWork() throws Exception {
		final InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("envoy-filter-issue99.yaml");
		final EnvoyFilter filter = mapper.readValue(inputStream, EnvoyFilter.class);

		final EnvoyConfigObjectPatch configObjectPatch = filter.getSpec().getConfigPatches().get(0);
		final EnvoyConfigObjectMatch match = configObjectPatch.getMatch();
		assertEquals(SIDECAR_OUTBOUND, match.getContext());


		/*
		value:
          name: istio.stats
          typed_config:
            '@type': type.googleapis.com/udpa.type.v1.TypedStruct
            type_url: type.googleapis.com/envoy.extensions.filters.http.wasm.v3.Wasm
            value:
              config:
                configuration: |
                  {
                    "debug": "false",
                    "stat_prefix": "istio"
                  }
                root_id: stats_outbound
                vm_config:
                  code:
                    local:
                      inline_string: envoy.wasm.stats
                  runtime: envoy.wasm.runtime.null
                  vm_id: stats_outbound
		 */
		final Map<String, Object> value = configObjectPatch.getPatch().getValue();
		final String actual = StructHelper.get(value, "typed_config.value.config.root_id", String.class);
		assertEquals("stats_outbound", actual);
	}
}
