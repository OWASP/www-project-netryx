package org.owasp.netryx.util.limiter;

import io.netty.channel.ChannelHandlerContext;
import org.owasp.netryx.model.LimiterConfig;
import org.owasp.netryx.util.InetAddressUtil;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicAddressLimiter
 * Limits the actions per IP address
 * @see LimiterConfig
 */
public class AtomicAddressLimiter implements Runnable, Closeable {
    private final Map<String, AddressInfo> addressInfoMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final LimiterConfig config;

    public AtomicAddressLimiter(LimiterConfig config) {
        this.config = config;
        scheduler.scheduleAtFixedRate(this, config.getCacheFlushMillis(), config.getCacheFlushMillis(), TimeUnit.MILLISECONDS);
    }

    public void increment(String address) {
        var info = addressInfoMap.computeIfAbsent(address, addr -> new AddressInfo());
        var currentTime = System.currentTimeMillis();

        if (currentTime < info.blockUntil)
            return;

        if (currentTime - info.lastRequestTimestamp > config.getCheckIntervalMillis())
            info.requestCount.set(0);

        info.requestCount.incrementAndGet();
        info.lastRequestTimestamp = currentTime;

        if (info.requestCount.get() > config.getMaxCount()) {
            info.blocked = true;
            info.blockUntil = currentTime + config.getBlockTimeSeconds() * 1000L;
        }
    }

    public boolean isBadAddress(String address) {
        var info = addressInfoMap.get(address);

        if (info == null || !info.blocked)
            return false;

        return System.currentTimeMillis() < info.blockUntil;
    }

    public boolean isBadAddress(ChannelHandlerContext ctx) {
        var remoteAddress = InetAddressUtil.extractIp(ctx);
        return isBadAddress(remoteAddress);
    }

    @Override
    public void run() {
        var currentTime = System.currentTimeMillis();

        addressInfoMap.entrySet().removeIf(entry ->
                entry.getValue().blocked && currentTime > entry.getValue().blockUntil
        );
    }

    @Override
    public void close() {
        addressInfoMap.clear();
        scheduler.shutdown();
    }

    private static class AddressInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private long lastRequestTimestamp = System.currentTimeMillis();

        private boolean blocked = false;
        private long blockUntil = 0;
    }
}