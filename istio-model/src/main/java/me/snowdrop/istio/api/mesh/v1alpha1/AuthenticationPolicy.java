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
 * AuthenticationPolicy defines authentication policy. It can be set for
 * different scopes (mesh, service ...), and the most narrow scope with
 * non-{@link #INHERIT} value will be used.
 * Mesh policy cannot be {@link #INHERIT}.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum AuthenticationPolicy {
    /**
     * Do not encrypt Envoy to Envoy traffic.
     */
    NONE(0),
    /**
     * Envoy to Envoy traffic is wrapped into mutual TLS connections.
     */
    MUTUAL_TLS(1),
    /**
     * Use the policy defined by the parent scope. Should not be used for mesh
     * policy.
     */
    INHERIT(1000);

    private final int intValue;

    AuthenticationPolicy(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
