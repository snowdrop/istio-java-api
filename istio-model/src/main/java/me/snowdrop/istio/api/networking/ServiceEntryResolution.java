package me.snowdrop.istio.api.networking;

public enum ServiceEntryResolution {
    /**
     * Assume that incoming connections have already been resolved (to a specific destination IP address).
     * Such connections are typically routed via the proxy using mechanisms such as IP table REDIRECT/ eBPF.
     * After performing any routing related transformations,
     * the proxy will forward the connection to the IP address to which the connection was bound.
     */
    NONE(0),
    /**
     * Use the static IP addresses specified in endpoints as the backing instances associated with the service.
     */
    STATIC(1),
    /**
     *Attempt to resolve the IP address by querying the ambient DNS, during request processing.
     * If no endpoints are specified,
     * the proxy will resolve the DNS address specified in the hosts field,
     * if wildcards are not used.
     * If endpoints are specified,
     * the DNS addresses specified in the endpoints will be resolved to determine the destination IP address.
     * DNS resolution cannot be used with unix domain socket endpoints.
     */
    DNS(2);

    private final int intValue;

    ServiceEntryResolution(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
