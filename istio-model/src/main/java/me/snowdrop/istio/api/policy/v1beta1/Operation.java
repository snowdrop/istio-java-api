/*
 *
 *  * Copyright (C) 2019 Red Hat, Inc.
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
package me.snowdrop.istio.api.policy.v1beta1;

/**
 * Header operation type.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Operation {
    /**
     * Replace a header by name.
     */
    REPLACE(0),

    /**
     * Remove a header by name. Values are ignored.
     */
    REMOVE(1),

    /**
     * Append values to the existing header values.
     */
    APPEND(2);

    private final int intValue;

    Operation(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
