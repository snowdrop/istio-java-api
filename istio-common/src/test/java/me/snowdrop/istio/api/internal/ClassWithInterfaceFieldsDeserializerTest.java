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

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.snowdrop.istio.api.test.AInterfaceType;
import me.snowdrop.istio.api.test.Class;
import me.snowdrop.istio.api.test.Interface;
import me.snowdrop.istio.api.test.Simple;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ClassWithInterfaceFieldsDeserializerTest {
    final YAMLMapper mapper = new YAMLMapper();

    @Test
    public void shouldDeserializeSimple() throws IOException {
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("simple.yml");
        final Simple simple = mapper.readValue(dataIs, Simple.class);
        assertThat(simple).isNotNull();
        assertThat(simple.aBoolean).isEqualTo(true);
        assertThat(simple.aInt).isEqualTo(1);
        assertThat(simple.aNumber).isEqualTo(2.0);
        assertThat(simple.aString).isEqualTo("foo");
    }

    @Test
    public void shouldDeserializeInterface() throws IOException {
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("interface.yml");
        final Interface value = mapper.readValue(dataIs, Interface.class);
        assertThat(value).isNotNull();
        final Interface.InterfaceType interfaceType = value.getInterfaceType();
        assertThat(interfaceType).isExactlyInstanceOf(AInterfaceType.class);
        final AInterfaceType a = (AInterfaceType) interfaceType;
        assertThat(a.getA()).isEqualTo("foo");
    }

    @Test
    public void shouldDeserializeClass() throws IOException {
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("class.yml");
        final Class value = mapper.readValue(dataIs, Class.class);
        assertThat(value).isNotNull();
        assertThat(value.simple.aString).isEqualTo("simple");
        assertThat(value.qualified.aBoolean).isEqualTo(false);
    }

    @Test
    public void shouldDeserializeMap() throws IOException {
        final InputStream dataIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("map.yml");
        final me.snowdrop.istio.api.test.Map value = mapper.readValue(dataIs, me.snowdrop.istio.api.test.Map.class);
        assertThat(value).isNotNull();
        assertThat(value.local.get("a").aInt).isEqualTo(1);
        assertThat(value.local.get("b").aInt).isEqualTo(0);
        assertThat(value.local.get("b").aString).isEqualTo("foo");
        assertThat(value.local.get("x")).isNull();
        assertThat(value.full.get("x").aBoolean).isEqualTo(false);
    }

}
