package me.snowdrop.istio.api.mesh.v1alpha1;

public enum H2UpgradePolicy {

    /**
     * Do not upgrade connections to http2.
     */
    DO_NOT_UPGRADE(0),

    /**
     * Upgrade the connections to http2.
     */
    UPGRADE(1);

    private final int intValue;

    H2UpgradePolicy(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
