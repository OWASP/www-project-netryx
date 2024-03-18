package org.owasp.netryx.model;

public class ValidatorConfig {
    private final int poolSize;
    private final long maxTime;

    public ValidatorConfig(int poolSize, long maxTime) {
        this.poolSize = poolSize;
        this.maxTime = maxTime;
    }

    public ValidatorConfig() {
        this(1, 100L);
    }

    public int getPoolSize() {
        return poolSize;
    }

    public long getMaxTime() {
        return maxTime;
    }
}
