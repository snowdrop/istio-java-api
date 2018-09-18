/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.mesh;

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
