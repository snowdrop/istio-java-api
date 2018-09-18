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
package me.snowdrop.istio.adapter.stackdriver;

/**
 * The kind of measurement. It describes how the data is reported.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum MetricKind {

    /**
     * Do not use this default value.
     */
    METRIC_KIND_UNSPECIFIED(0),

    /**
     * An instantaneous measurement of a value.
     */
    GAUGE(1),

    /**
     * The change in a value during a time interval.
     */
    DELTA(2),

    /**
     * A value accumulated over a time interval.  Cumulative
     * measurements in a time series should have the same start time
     * and increasing end times, until an event resets the cumulative
     * value to zero and sets a new start time for the following
     * points.
     */
    CUMULATIVE(3);

    private final int intValue;

    MetricKind(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
