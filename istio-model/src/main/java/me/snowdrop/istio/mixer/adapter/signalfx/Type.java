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
package me.snowdrop.istio.mixer.adapter.signalfx;

/**
 * Describes what kind of metric this is.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Type {
    /**
     * None is the default and is invalid
     */
    NONE(0),

    /**
     * Values with the same set of dimensions will be added together
     * as a continuously incrementing value.
     */
    COUNTER(1),

    /**
     * A histogram distribution.  This will result in several metrics
     * emitted for each unique set of dimensions.
     */
    HISTOGRAM(2);

    private final int intValue;

    Type(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
