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
package me.snowdrop.istio.api.mesh.v1alpha1;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Mode {
    /**
     * outbound traffic will be restricted to services defined in the service registry as well as those defined
     * through ServiceEntries
     */
    REGISTRY_ONLY(0),
    /**
     * outbound traffic to unknown destinations will be allowed
     */
    ALLOW_ANY(1),
    /**
     * not implemented. outbound traffic will be restricted to destinations defined in VirtualServices only
     */
    VIRTUAL_SERVICE_ONLY(2);

    private final int intValue;

    Mode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
