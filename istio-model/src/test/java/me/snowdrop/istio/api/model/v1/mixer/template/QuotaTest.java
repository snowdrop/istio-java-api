/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioResourceBuilder;
import me.snowdrop.istio.api.model.v1.cexl.TypedValue;
import me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class QuotaTest extends BaseIstioTest {
    @Test
    public void quotaRoundtripShouldWork() throws Exception {
        /*

        apiVersion: "config.istio.io/v1alpha2"
kind: quota
metadata:
  name: requestcount
  namespace: istio-config-default
spec:
  dimensions:
    source: source.labels["app"] | source.service | "unknown"
    sourceVersion: source.labels["version"] | "unknown"
    destination: destination.labels["app"] | destination.service | "unknown"
    destinationVersion: destination.labels["version"] | "unknown"
         */

        IstioResource quota = new IstioResourceBuilder()
                .withNewMetadata()
                .withName("requestcount")
                .withNamespace("istio-config-default")
                .endMetadata()
                .withNewQuotaSpec()
                .addToDimensions("source", new TypedValue(ValueType.STRING, "source.labels[\"app\"] | source.service | \"unknown\""))
                .addToDimensions("sourceVersion", new TypedValue(ValueType.STRING, "source.labels[\"version\"] | \"unknown\""))
                .addToDimensions("destination", new TypedValue(ValueType.STRING, "destination.labels[\"app\"] | destination.service | \"unknown\""))
                .addToDimensions("destinationVersion", new TypedValue(ValueType.STRING, "destination.labels[\"version\"] \"unknown\""))
                .endQuotaSpec()
                .build();


        final String output = mapper.writeValueAsString(quota);

        IstioResource reloaded = mapper.readValue(output, IstioResource.class);

        assertEquals(quota, reloaded);
    }
}
