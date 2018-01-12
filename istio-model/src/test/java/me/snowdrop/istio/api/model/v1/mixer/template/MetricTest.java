/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

import me.snowdrop.istio.api.model.IstioBaseResourceBuilder;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.v1.cexl.TypedValue;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class MetricTest extends BaseIstioTest {
    @Test
    public void metricRoundtripShouldWork() throws Exception {
        /*
    apiVersion: "config.istio.io/v1alpha2"
kind: metric
metadata:
  name: requestsize
  namespace: istio-config-default
spec:
  value: request.size | 0
  dimensions:
    sourceService: source.service | "unknown"
    sourceVersion: source.labels["version"] | "unknown"
    destinationService: destination.service | "unknown"
    destinationVersion: destination.labels["version"] | "unknown"
    responseCode: response.code | 200
  monitoredResourceType: '"UNSPECIFIED"'
     */

        IstioResource resource = new IstioBaseResourceBuilder()
                .withNewMetadata()
                .withName("requestsize")
                .withNamespace("istio-config-default")
                .endMetadata()
                .withNewMetricSpec()
                .withValue(TypedValue.from("request.size | 0"))
                .addToDimensions("sourceService", TypedValue.from("source.service | \"unknown\""))
                .addToDimensions("sourceVersion", TypedValue.from("source.labels[\"version\"] | \"unknown\""))
                .addToDimensions("destinationService", TypedValue.from("destination.service | \"unknown\""))
                .addToDimensions("destinationVersion", TypedValue.from("destination.labels[\"version\"] | \"unknown\""))
                .addToDimensions("responseCode", TypedValue.from("response.code | 200"))
                .withMonitoredResourceType("UNSPECIFIED")
                .endMetricSpec()
                .build();

        final String output = mapper.writeValueAsString(resource);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(resource, reloaded);
    }
}
