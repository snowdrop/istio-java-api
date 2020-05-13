package me.snowdrop.istio.api.networking.v1beta1;

public enum H2UpgradePolicy {

    /**
     * Use the global default.
     */
    DEFAULT(0),

    /**
     * Do not upgrade the connection to http2.
     * This opt-out option overrides the default.
     */
    DO_NOT_UPGRADE(1),

    /**
     * Upgrade the connection to http2.
     * This opt-in option overrides the default.
     */
    UPGRADE(2);

    private final int intValue;

    H2UpgradePolicy(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
