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
package me.snowdrop.istio.adapter.stackdriver;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum ValueType {

    /**
     * Do not use this default value.
     */
    VALUE_TYPE_UNSPECIFIED(0),

    /**
     * The value is a boolean.
     * This value type can be used only if the metric kind is `GAUGE`.
     */
    BOOL(1),

    /**
     * The value is a signed 64-bit integer.
     */
    INT64(2),

    /**
     * The value is a double precision floating point number.
     */
    DOUBLE(3),

    /**
     * The value is a text string.
     * This value type can be used only if the metric kind is `GAUGE`.
     */
    STRING(4),

    /**
     * The value is a [`Distribution`][google.api.Distribution].
     */
    DISTRIBUTION(5),

    /**
     * The value is money.
     */
    MONEY(6);

    private final int intValue;

    ValueType(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
