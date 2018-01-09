/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.config.descriptor;

/**
 * The kind of measurement. It describes how the data is recorded.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum MetricKind {
    /**
     * Do not use this default value.
     */
    METRIC_KIND_UNSPECIFIED,
    /**
     * An instantaneous measurement of a value. For example, the number of VMs.
     */
    GAUGE,
    /**
     * A count of occurrences over an interval, always a positive integer. For example, the number of API requests.
     */
    COUNTER,
    /**
     * Summary statistics for a population of values. At the moment, only
     * histograms representing the distribution of those values across a set of
     * buckets are supported (configured via the buckets field).
     * <p>
     * Values for DISTRIBUTIONs will be reported in singular form. It will be up
     * to the mixer adapters and backend systems to transform single reported
     * values into the distribution form as needed (and as supported).
     */
    DISTRIBUTION
}
