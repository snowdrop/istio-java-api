/*
 *
 *  * Copyright (C) 2019 Red Hat, Inc.
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
package me.snowdrop.istio.api.networking.v1beta1;

/**
 * CaptureMode describes how traffic to a listener is expected to be
 * captured. Applicable only when the listener is bound to an IP.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum CaptureMode {
    /**
     * The default capture mode defined by the environment
     */
    CaptureMode_DEFAULT(0),

    /**
     * Capture traffic using IPtables redirection
     */
    CaptureMode_IPTABLES(1),

    /**
     * No traffic capture. When used in egress listener, the application is
     * expected to explicitly communicate with the listener port/unix
     * domain socket. When used in ingress listener, care needs to be taken
     * to ensure that the listener port is not in use by other processes on
     * the host.
     */
    CaptureMode_NONE(2);

    private final int intValue;

    CaptureMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
