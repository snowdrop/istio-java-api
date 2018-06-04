/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mesh;

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
