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
package me.snowdrop.istio.api.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.snowdrop.istio.api.internal.ClassWithInterfaceFieldsDeserializer;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonDeserialize(using = ClassWithInterfaceFieldsDeserializer.class)
public class Simple {
    public int aInt;

    public double aNumber;

    public boolean aBoolean;

    public String aString;
}
