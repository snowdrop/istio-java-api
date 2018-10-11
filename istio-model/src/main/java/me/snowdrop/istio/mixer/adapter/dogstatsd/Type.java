/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.mixer.adapter.dogstatsd;

/**
 * Describes the type of metric
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Type {

    /**
     * Default Unknown Type
     */
    UNKNOWN_TYPE(0),
    /**
     * Increments a DataDog counter
     */
    COUNTER(1),
    /**
     * Sets the new value of a DataDog gauge
     */
    GAUGE(2),
    /**
     * DISTRIBUTION is converted to a Timing Histogram for metrics with a time unit and a Histogram for all other units
     */
    DISTRIBUTION(3);

    private final int intValue;

    Type(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
