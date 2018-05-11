/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.adapter.prometheus;

/**
 * Describes what kind of metric this is.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Kind {
    UNSPECIFIED(0),
    GAUGE(1),
    COUNTER(2),
    DISTRIBUTION(3);

    private final int intValue;

    Kind(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
