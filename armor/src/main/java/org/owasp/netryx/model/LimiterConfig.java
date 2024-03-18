package org.owasp.netryx.model;

public class LimiterConfig {
    private final boolean enabled;
    private final int maxCount;
    private final long checkIntervalMillis;
    private final int blockTimeSeconds;
    private final long cacheFlushMillis;

    public LimiterConfig() {
        this(true, 5, 1000L, 10, 300000L);
    }

    public LimiterConfig(boolean enabled, int maxCount, long checkIntervalMillis,
                         int blockTimeSeconds, long cacheFlushMillis) {
        this.enabled = enabled;
        this.maxCount = maxCount;
        this.checkIntervalMillis = checkIntervalMillis;
        this.blockTimeSeconds = blockTimeSeconds;
        this.cacheFlushMillis = cacheFlushMillis;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public long getCheckIntervalMillis() {
        return checkIntervalMillis;
    }

    public int getBlockTimeSeconds() {
        return blockTimeSeconds;
    }

    public long getCacheFlushMillis() {
        return cacheFlushMillis;
    }
}
