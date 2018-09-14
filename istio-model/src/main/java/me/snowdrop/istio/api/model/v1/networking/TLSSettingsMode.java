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
package me.snowdrop.istio.api.model.v1.networking;

/**
 * TLS connection mode
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum TLSSettingsMode {
    /**
     * Do not setup a TLS connection to the upstream endpoint.
     */
    DISABLE(0),

    /**
     * Originate a TLS connection to the upstream endpoint.
     */
    SIMPLE(1),

    /**
     * Secure connections to the upstream using mutual TLS by presenting
     * client certificates for authentication.
     */
    MUTUAL(2),

    /**
     * Secure connections to the upstream using mutual TLS by presenting
     * client certificates for authentication.
     * Compared to Mutual mode, this mode uses certificates generated
     * automatically by Istio for mTLS authentication. When this mode is
     * used, all other fields in `TLSSettings` should be empty.
     */
    ISTIO_MUTUAL(3);

    private final int intValue;

    TLSSettingsMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
