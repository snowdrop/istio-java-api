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
package me.snowdrop.istio.api.authentication;

/**
 * Associates authentication with request principal.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum PrincipalBinding {


    /**
     * Principal will be set to the identity from peer authentication.
     */
    USE_PEER(0),

    /**
     * Principal will be set to the identity from origin authentication.
     */
    USE_ORIGIN(1);


    private final int intValue;

    PrincipalBinding(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
