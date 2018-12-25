package me.snowdrop.istio.api.test;

/**
 * @author <a href="wengyanghui@foxmail.com">Young Weng</a>
 */
public enum Enum {
    A(0);

    private final int intValue;

    Enum(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
