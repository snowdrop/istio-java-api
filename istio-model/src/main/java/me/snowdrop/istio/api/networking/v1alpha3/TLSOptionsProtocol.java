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
package me.snowdrop.istio.api.networking.v1alpha3;

/**
 * TLS protocol versions.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum TLSOptionsProtocol {

    /**
     * Automatically choose the optimal TLS version.
     */
    Server_TLSOptions_TLS_AUTO(0),

    /**
     * TLS version 1.0
     */
    Server_TLSOptions_TLSV1_0(1),

    /**
     * TLS version 1.1
     */
    Server_TLSOptions_TLSV1_1(2),

    /**
     * TLS version 1.2
     */
    Server_TLSOptions_TLSV1_2(3),

    /**
     * TLS version 1.3
     */
    Server_TLSOptions_TLSV1_3(4);

    private final int intValue;

    TLSOptionsProtocol(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
