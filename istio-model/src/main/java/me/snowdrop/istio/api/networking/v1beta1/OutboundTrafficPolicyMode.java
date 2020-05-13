package me.snowdrop.istio.api.networking.v1beta1;

public enum OutboundTrafficPolicyMode {

    /**
     * Outbound traffic will be restricted to services defined in the
     * service registry as well as those defined through `ServiceEntry` configurations.
     */
    REGISTRY_ONLY(0),

    /**
     * Outbound traffic to unknown destinations will be allowed, in case
     * there are no services or `ServiceEntry` configurations for the destination port.
     */
    ALLOW_ANY(1);

    private final int intValue;

    OutboundTrafficPolicyMode(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
