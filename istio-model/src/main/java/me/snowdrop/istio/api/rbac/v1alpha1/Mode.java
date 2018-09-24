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
package me.snowdrop.istio.api.rbac.v1alpha1;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Mode {
    /**
     * Disable Istio RBAC completely, any other config in RbacConfig will be ignored and Istio RBAC policies
     * will not be enforced.
     */
    OFF(0),

    /**
     * Enable Istio RBAC for all services and namespaces.
     */
    ON(1),

    /**
     * Enable Istio RBAC only for services and namespaces specified in the inclusion field. Any other
     * services and namespaces not in the inclusion field will not be enforced by Istio RBAC policies.
     */
    ON_WITH_INCLUSION(2),

    /**
     * Enable Istio RBAC for all services and namespaces except those specified in the exclusion field. Any other
     * services and namespaces not in the exclusion field will be enforced by Istio RBAC policies.
     */
    ON_WITH_EXCLUSION(3);

    private final int intValue;

    Mode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
