/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.adapter.prometheus;

import java.io.InputStream;

import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class PrometheusTest extends BaseIstioTest {
    @Test
    public void metricRoundtripShouldWork() throws Exception {
        /*
    apiVersion: "config.istio.io/v1alpha2"
kind: prometheus
metadata:
  name: recommendationrequestcounthandler
  namespace: istio-system
spec:
  metrics:
  - name: recommendation_request_count
    instance_name: recommendationrequestcount.metric.istio-system
    kind: COUNTER
    label_names:
    - source
    - destination
    - user_agent
    - version
     */

        IstioResource resource = new IstioResourceBuilder()
                .withNewMetadata()
                .withName("recommendationrequestcounthandler")
                .withNamespace("istio-system")
                .endMetadata()
                .withNewPrometheusSpec()
                .addNewMetric()
                .withName("recommendation_request_count")
                .withInstanceName("recommendationrequestcount.metric.istio-system")
                .withKind(Kind.COUNTER)
                .addToLabelNames("source", "destination", "user_agent", "version")
                .endMetric()
                .endPrometheusSpec()
                .build();

        final String output = mapper.writeValueAsString(resource);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(resource, reloaded);
    }

    @Test
    public void loadingFromYAMLShouldWork() throws Exception {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("prometheus.yaml");
        final IstioResource metricRes = mapper.readValue(is, IstioResource.class);

        assertEquals(metricRes.getKind(), "prometheus");

        final IstioSpec spec = metricRes.getSpec();
        assertTrue(spec instanceof Prometheus);

        final Prometheus prometheus = (Prometheus) spec;
        assertEquals(1, prometheus.getMetrics().size());
        final MetricInfo metricInfo = prometheus.getMetrics().get(0);
        assertEquals("recommendation_request_count", metricInfo.getName());
        assertEquals("recommendationrequestcount.metric.istio-system", metricInfo.getInstanceName());
        assertEquals(Kind.COUNTER, metricInfo.getKind());
        assertEquals(4, metricInfo.getLabelNames().size());
    }
}
