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
package me.snowdrop.istio.api.policy.v1beta1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import me.snowdrop.istio.mixer.adapter.prometheus.Kind;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class HandlerTest extends BaseIstioTest {
	@Test
	public void roundtripShouldWork() throws JsonProcessingException {
		/*
		apiVersion: config.istio.io/v1alpha2
kind: handler
metadata:
  name: doublehandler
  namespace: istio-system
spec:
  compiledAdapter: prometheus
  params:
    metrics:
    - name: double_request_count # Prometheus metric name
      instance_name: doublerequestcount.instance.istio-system # Mixer instance name (fully-qualified)
      kind: COUNTER
      label_names:
      - reporter
      - source
      - destination
      - message
		 */
		final Handler handler = new HandlerBuilder()
				.withNewMetadata().withName("doublehandler").endMetadata()
				.withNewSpec()
				.withCompiledAdapter(SupportedAdapters.PROMETHEUS)
				.withNewPrometheusParams()
				.addNewMetric()
				.withName("double_request_count")
				.withInstanceName("doublerequestcount.instance.istio-system")
				.withKind(Kind.COUNTER)
				.withLabelNames("reporter", "source", "destination", "message")
				.endMetric()
				.endPrometheusParams()
				.endSpec()
				.build();

		final String output = mapper.writeValueAsString(handler);

		HasMetadata reloaded = mapper.readValue(output, HasMetadata.class);

		assertEquals(handler, reloaded);
	}
}
