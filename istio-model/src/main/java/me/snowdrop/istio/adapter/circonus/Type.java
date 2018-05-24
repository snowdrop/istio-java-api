/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.adapter.circonus;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Type {
    UNKNOWN(0),
    COUNTER(1),
    GAUGE(2),
    DISTRIBUTION(3);

    private final int intValue;

    Type(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
