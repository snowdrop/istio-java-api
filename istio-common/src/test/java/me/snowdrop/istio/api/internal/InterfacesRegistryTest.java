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

import java.util.Set;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class InterfacesRegistryTest {
    @Test
    public void loadingResourceShouldWork() {
        final Set<String> knownClasses = InterfacesRegistry.getKnownClasses();
        assertThat(knownClasses).isNotEmpty();
        assertThat(knownClasses).size().isEqualTo(4);
        assertThat(knownClasses).contains(
                "me.snowdrop.istio.api.test.Simple",
                "me.snowdrop.istio.api.test.Interface",
                "me.snowdrop.istio.api.test.Class",
                "me.snowdrop.istio.api.test.Map");
    }

    @Test
    public void simpleClassShouldDefineSimpleFields() {
        final String targetClassName = "me.snowdrop.istio.api.test.Simple";
        checkFieldFor(targetClassName, "aInt", "aInt", "integer");
        checkFieldFor(targetClassName, "aNumber", "aNumber", "number");
        checkFieldFor(targetClassName, "aString", "aString", "string");
        checkFieldFor(targetClassName, "aBoolean", "aBoolean", "boolean");

        Throwable thrown = catchThrowable(() -> InterfacesRegistry.getFieldInfo(targetClassName, "foo"));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasNoCause().hasMessageContaining("foo");
    }

    private void checkFieldFor(String targetClassName, String field, String expectedTarget, String expectedType) {
        InterfacesRegistry.FieldInfo info = InterfacesRegistry.getFieldInfo(targetClassName, field);
        assertThat(info).isNotNull();
        assertThat(info.target()).isEqualTo(expectedTarget);
        assertThat(info.type()).isEqualTo(expectedType);
    }
}
