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
package me.snowdrop.istio.api.authentication.v1alpha1;

/**
 * Defines the acceptable connection TLS mode.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Mode {

    /**
     * Client cert must be presented, connection is in TLS.
     */
    STRICT(0),

    /**
     * Connection can be either plaintext or TLS, and client cert can be omitted.
     */
    PERMISSIVE(1);

    private final int intValue;

    Mode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
