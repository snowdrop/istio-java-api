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
package me.snowdrop.istio.api.internal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioRegistryTest {

    @Test
    public void shouldReturnKindWithProperCase() {
        assertThat(IstioSpecRegistry.getCRDInfo("Handler", "v1alpha2").get().getKind()).isEqualTo("handler");
        assertThat(IstioSpecRegistry.getCRDInfo("Handler", "v1beta1").get().getKind()).isEqualTo("handler");
        assertThat(IstioSpecRegistry.getCRDInfo("Gateway", "v1alpha3").get().getKind()).isEqualTo("Gateway");
        assertThat(IstioSpecRegistry.getCRDInfo("Gateway", "v1beta1").get().getKind()).isEqualTo("Gateway");
    }
}
