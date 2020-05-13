package me.snowdrop.istio.api.security.v1beta1;

public enum Action {

    /**
     * Allow a request only if it matches the rules. This is the default type.
     */
    ALLOW(0),

    /**
     * Deny a request if it matches any of the rules.
     */
    DENY(1);

    private final int intValue;

    Action(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
