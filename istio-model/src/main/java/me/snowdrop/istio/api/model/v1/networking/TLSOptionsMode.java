package me.snowdrop.istio.api.model.v1.networking;

public enum TLSOptionsMode {
    /**
     * Forward the connection to the upstream server selected based on the SNI string presented by the client.
     */
    PASSTHROUGH(0),
    /**
     * Secure connections with standard TLS semantics.
     */
    SIMPLE(1),
    /**
     * Secure connections to the upstream using mutual TLS by presenting client certificates for authentication.
     */
    MUTUAL(2);

    private final int intValue;

    TLSOptionsMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
