package me.snowdrop.istio.api.networking.v1alpha3;

public enum ServerTLSSettingsMode {

    /**
     * The SNI string presented by the client will be used as the match criterion in a VirtualService TLS route to
     * determine the destination service from the service registry.
     */
    PASSTHROUGH(0),

    /**
     * Secure connections with standard TLS semantics.
     */
    SIMPLE(1),

    /**
     * Secure connections to the downstream using mutual TLS by presenting
     * server certificates for authentication.
     */
    MUTUAL(2),

    /**
     * Similar to the passthrough mode, except servers with this TLS mode
     * do not require an associated VirtualService to map from the SNI
     * value to service in the registry. The destination details such as
     * the service/subset/port are encoded in the SNI value. The proxy
     * will forward to the upstream (Envoy) cluster (a group of
     * endpoints) specified by the SNI value. This server is typically
     * used to provide connectivity between services in disparate L3
     * networks that otherwise do not have direct connectivity between
     * their respective endpoints. Use of this mode assumes that both the
     * source and the destination are using Istio mTLS to secure traffic.
     */
    AUTO_PASSTHROUGH(3),

    /**
     * Secure connections from the downstream using mutual TLS by presenting
     * server certificates for authentication.
     * Compared to Mutual mode, this mode uses certificates, representing
     * gateway workload identity, generated automatically by Istio for
     * mTLS authentication. When this mode is used, all other fields in
     * `TLSOptions` should be empty.
     */
    ISTIO_MUTUAL(4);

    private final int intValue;

    ServerTLSSettingsMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
