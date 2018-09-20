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
 * The mode used to redirect inbound traffic to Envoy.
 * This setting has no effect on outbound traffic: {@code iptables REDIRECT} is always used for outbound connections.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum InboundInterceptionMode {
    /**
     * The REDIRECT mode uses iptables REDIRECT to NAT and redirect to Envoy. This mode loses source IP addresses during redirection.
     */
    REDIRECT(0),
    /**
     * The TPROXY mode uses iptables TPROXY to redirect to Envoy. This mode preserves both the source and destination IP
     * addresses and ports, so that they can be used for advanced filtering and manipulation. This mode also configures the
     * sidecar to run with the CAP_NET_ADMIN capability, which is required to use TPROXY.
     */
    TPROXY(1);

    private final int intValue;

    InboundInterceptionMode(int value) {
        this.intValue = value;
    }

    public int value() {
        return intValue;
    }
}
