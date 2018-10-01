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
package me.snowdrop.istio.client.it;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.cexl.AttributeVocabulary;
import me.snowdrop.istio.api.cexl.TypedValue;
import me.snowdrop.istio.client.IstioClient;
import me.snowdrop.istio.client.KubernetesAdapter;
import me.snowdrop.istio.mixer.template.metric.Metric;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class MetricIT {
    private final IstioClient istioClient
            = new IstioClient(new KubernetesAdapter(new DefaultKubernetesClient()));

    /*
apiVersion: "config.istio.io/v1alpha2"
kind: metric
metadata:
  name: recommendationrequestcount
  namespace: istio-system
spec:
  value: "1"
  dimensions:
    source: source.service | "unknown"
    destination: destination.service | "unknown"
    version: destination.labels["version"] | "unknown"
    user_agent: request.headers["user-agent"] | "unknown"
  monitoredResourceType: '"UNSPECIFIED"'
     */
    @Test
    public void checkBasicMetric() {
        //given
        final IstioResource metric = new IstioResourceBuilder()
                .withApiVersion("config.istio.io/v1alpha2")
                .withNewMetadata()
                .withName("recommendationrequestcount")
                .withNamespace("istio-system")
                .endMetadata()
                .withNewMetricSpec()
                .withNewValue().withExpression("1").endValue()
                .addToDimensions("source", TypedValue.from(AttributeVocabulary.source_service + "|\"unknown\""))
                .addToDimensions("destination", TypedValue.from(AttributeVocabulary.destination_service + "|\"unknown \""))
                .addToDimensions("version", TypedValue.from(AttributeVocabulary.destination_labels + "[\"version\"] | \"unknown\""))
                .addToDimensions("user_agent", TypedValue.from(AttributeVocabulary.request_headers + "[\"user-agent\"]|\"unknown\""))
                .withMonitoredResourceType("UNSPECIFIED")
                .endMetricSpec()
                .build();

        //when
        final IstioResource resultResource = istioClient.registerCustomResource(metric);

        //then
        assertThat(resultResource).isNotNull().satisfies(istioResource -> {

            assertThat(istioResource.getKind()).isEqualTo("metric");

            assertThat(istioResource)
                    .extracting("metadata")
                    .extracting("name")
                    .containsOnly("recommendationrequestcount");
        });

        //and
        final IstioSpec resultSpec = resultResource.getSpec();
        assertThat(resultSpec).isNotNull().isInstanceOf(Metric.class);

        //and

        assertThat((Metric) resultSpec).satisfies(ms -> {

            assertThat(ms.getValue().getExpression()).isEqualTo("1");

            assertThat(ms.getDimensions()).satisfies(dimensions -> {

                assertThat(dimensions).hasSize(4);
                assertThat(dimensions).containsKeys("source", "destination", "version", "user_agent");
                assertThat(dimensions.get("source").getExpression()).isEqualTo("source.service | \"unknown\"");
            });

        });

        //when
        final Boolean deleteResult = istioClient.unregisterCustomResource(resultResource);

        //then
        assertThat(deleteResult).isTrue();
    }
}
