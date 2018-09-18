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
package me.snowdrop.istio.api.rbac;

/**
 * RBAC ServiceRoleBinding enforcement mode, used to verify new ServiceRoleBinding
 * configs work as expected before rolling to production. RBAC engine only logs results
 * from configs that are in permissive mode, and discards result before returning
 * to the user.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum EnforcementMode {
    /**
     * Policy in ENFORCED mode has impact on user experience.
     * Policy is in ENFORCED mode by default.
     */
    ENFORCED(0),

    /**
     * Policy in PERMISSIVE mode isn't enforced and has no impact on users.
     * RBAC engine run policies in PERMISSIVE mode and logs stats.
     */
    PERMISSIVE(1);

    private final int intValue;

    EnforcementMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
