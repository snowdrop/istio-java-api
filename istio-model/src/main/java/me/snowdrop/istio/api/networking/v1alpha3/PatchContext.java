package me.snowdrop.istio.api.networking.v1alpha3;

public enum PatchContext {

    /**
     * All listeners/routes/clusters in both sidecars and gateways.
     */
    ANY(0),

    /**
     * Inbound listener/route/cluster in sidecar.
     */
    SIDECAR_INBOUND(1),

    /**
     * Outbound listener/route/cluster in sidecar.
     */
    SIDECAR_OUTBOUND(2),

    /**
     * Gateway listener/route/cluster.
     */
    GATEWAY(3);

    private final int intValue;

    PatchContext(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
