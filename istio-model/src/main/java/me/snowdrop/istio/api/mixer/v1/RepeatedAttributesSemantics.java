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
package me.snowdrop.istio.api.mixer.v1;

/**
 * Used to signal how the sets of compressed attributes should be reconstitued server-side.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum RepeatedAttributesSemantics {
    /**
     * Use delta encoding between sets of compressed attributes to reduce the overall on-wire
     * request size. Each individual set of attributes is used to modify the previous set.
     * NOTE: There is no way with this encoding to specify attribute value deletion. This
     * option should be used with extreme caution.
     */
    DELTA_ENCODING(0),

    /**
     * Treat each set of compressed attributes as complete - independent from other sets
     * in this request. This will result in on-wire duplication of attributes and values, but
     * will allow for proper accounting of absent values in overall encoding.
     */
    INDEPENDENT_ENCODING(1);

    private final int intValue;

    RepeatedAttributesSemantics(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
