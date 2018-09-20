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

public enum ServiceEntryLocation {
    /**
     * Signifies that the service is external to the mesh.
     * Typically used to indicate external services consumed through APIs.
     */
    MESH_EXTERNAL(0),
    /**
     * Signifies that the service is part of the mesh.
     * Typically used to indicate services added explicitly
     * as part of expanding the service mesh
     * to include unmanaged infrastructure (e.g., VMs added to a Kubernetes based service mesh).
     */
    MESH_INTERNAL(1);

    private final int intValue;

    ServiceEntryLocation(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
