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
package me.snowdrop.istio.api.networking;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum SimpleLB {
    /**
     * Round Robin policy. Default
     */
    ROUND_ROBIN(0),

    /**
     * The least request load balancer uses an O(1) algorithm which selects
     * two random healthy hosts and picks the host which has fewer active
     * requests.
     */
    LEAST_CONN(1),

    /**
     * The random load balancer selects a random healthy host. The random
     * load balancer generally performs better than round robin if no health
     * checking policy is configured.
     */
    RANDOM(2),

    /**
     * This option will forward the connection to the original IP address
     * requested by the caller without doing any form of load
     * balancing. This option must be used with care. It is meant for
     * advanced use cases. Refer to Original Destination load balancer in
     * Envoy for further details.
     */
    PASSTHROUGH(3);

    private final int intValue;

    SimpleLB(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
