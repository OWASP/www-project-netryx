package org.owasp.netryx.model.settings;

public class Http1Settings {
    private final int maxObjectSize;

    public Http1Settings() {
        this(65536);
    }

    public Http1Settings(int maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }

    public int getMaxObjectSize() {
        return maxObjectSize;
    }
}