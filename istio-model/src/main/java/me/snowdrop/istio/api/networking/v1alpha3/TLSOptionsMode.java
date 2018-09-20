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

package me.snowdrop.istio.api.networking.v1alpha3;

public enum TLSOptionsMode {
    /**
     * Forward the connection to the upstream server selected based on the SNI string presented by the client.
     */
    PASSTHROUGH(0),
    /**
     * Secure connections with standard TLS semantics.
     */
    SIMPLE(1),
    /**
     * Secure connections to the upstream using mutual TLS by presenting client certificates for authentication.
     */
    MUTUAL(2);

    private final int intValue;

    TLSOptionsMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
