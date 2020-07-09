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
import me.snowdrop.istio.api.cexl.TypedValue;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class InstanceTest extends BaseIstioTest {
	@Test
	public void roundtripShouldWork() throws JsonProcessingException {
		/*
		apiVersion: config.istio.io/v1alpha2
kind: instance
metadata:
  name: doublerequestcount
  namespace: istio-system
spec:
  compiledTemplate: metric
  params:
    value: "2" # count each request twice
    dimensions:
      reporter: conditional((context.reporter.kind | "inbound") == "outbound", "client", "server")
      source: source.workload.name | "unknown"
      destination: destination.workload.name | "unknown"
      message: '"twice the fun!"'
    monitored_resource_type: '"UNSPECIFIED"'
		 */
		final Instance instance = new InstanceBuilder()
				.withApiVersion("config.istio.io/v1alpha2")
				.withNewMetadata().withName("doublerequestcount").withNamespace("istio-system").endMetadata()
				.withNewSpec().withCompiledTemplate(SupportedTemplates.METRIC)
				.withNewMetricParams().withValue(TypedValue.from("2"))
				.addToDimensions("reporter", TypedValue.unparsed("conditional((context.reporter.kind | \"inbound\") == \"outbound\", \"client\", \"server\")"))
				.addToDimensions("source", TypedValue.from("source.workload.name | \"unknown\""))
				.addToDimensions("destination", TypedValue.from("destination.workload.name | \"unknown\""))
				.addToDimensions("message", TypedValue.from("\"twice the fun!\""))
				.withMonitoredResourceType("UNSPECIFIED")
				.endMetricParams()
				.endSpec()
				.build();

		final String output = mapper.writeValueAsString(instance);

		HasMetadata reloaded = mapper.readValue(output, HasMetadata.class);

		assertEquals(instance, reloaded);
	}
}
