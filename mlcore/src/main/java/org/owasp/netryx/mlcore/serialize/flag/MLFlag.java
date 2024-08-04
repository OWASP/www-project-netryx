package org.owasp.netryx.mlcore.serialize.flag;

public final class MLFlag {
    private MLFlag() {}

    public static final int START_MODEL = 0xABCD001;
    public static final int END_MODEL = 0xABCD0002;

    public static final int START_REGULARIZER = 0xABCD003;
    public static final int END_REGULARIZER = 0xABCD0004;

    public static final int START_MATRIX = 0xABCD005;
    public static final int END_MATRIX = 0xABCD0006;

    public static final int START_OPTIMIZER = 0xABCD007;
    public static final int END_OPTIMIZER = 0xABCD0008;

    public static final int START_ENCODER = 0xABCD009;
    public static final int END_ENCODER = 0xABCD0010;

    public static void ensureStartModel(int flag) {
        if (flag != START_MODEL)
            throw new IllegalArgumentException("Not START_MODEL flag: " + flag);
    }

    public static void ensureEndModel(int flag) {
        if (flag != END_MODEL)
            throw new IllegalArgumentException("Not END_MODEL flag: " + flag);
    }

    public static void ensureStartRegularization(int flag) {
        if (flag != START_REGULARIZER)
            throw new IllegalArgumentException("Not START_REGULARIZATION flag: " + flag);
    }

    public static void ensureEndRegularization(int flag) {
        if (flag != END_REGULARIZER)
            throw new IllegalArgumentException("Not END_REGULARIZATION flag: " + flag);
    }

    public static void ensureStartMatrix(int flag) {
        if (flag != START_MATRIX)
            throw new IllegalArgumentException("Not START_MATRIX flag: " + flag);
    }

    public static void ensureEndMatrix(int flag) {
        if (flag != END_MATRIX)
            throw new IllegalArgumentException("Not END_MATRIX flag: " + flag);
    }

    public static void ensureStartOptimizer(int flag) {
        if (flag != START_OPTIMIZER)
            throw new IllegalArgumentException("Not START_OPTIMIZER flag: " + flag);
    }

    public static void ensureEndOptimizer(int flag) {
        if (flag != END_OPTIMIZER)
            throw new IllegalArgumentException("Not END_OPTIMIZER flag: " + flag);
    }

    public static void ensureStartEncoder(int flag) {
        if (flag != START_ENCODER)
            throw new IllegalArgumentException("Not START_OPTIMIZER flag: " + flag);
    }

    public static void ensureEndEncoder(int flag) {
        if (flag != END_ENCODER)
            throw new IllegalArgumentException("Not END_OPTIMIZER flag: " + flag);
    }
}
