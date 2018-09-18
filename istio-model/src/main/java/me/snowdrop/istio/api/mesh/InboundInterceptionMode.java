/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.mesh;

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
