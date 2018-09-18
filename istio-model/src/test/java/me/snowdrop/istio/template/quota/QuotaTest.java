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
package me.snowdrop.istio.template.quota;

import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioResourceBuilder;
import me.snowdrop.istio.api.cexl.TypedValue;
import me.snowdrop.istio.api.mixer.config.descriptor.ValueType;
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
