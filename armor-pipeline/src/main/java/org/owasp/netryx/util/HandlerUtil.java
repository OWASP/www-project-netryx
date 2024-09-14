package org.owasp.netryx.util;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import java.util.Map;

public final class HandlerUtil {
    private HandlerUtil() {}

    public static <T extends ChannelHandler> Map.Entry<String, T> findHandler(ChannelPipeline pipeline, Class<T> clazz) {
        for (var entry : pipeline) {
            var handler = entry.getValue();

            if (clazz.isInstance(handler))
                return Map.entry(entry.getKey(), clazz.cast(handler));
        }

        return null;
    }

    public static <T extends ChannelHandler> String hasHandler(ChannelPipeline pipeline, Class<T> clazz) {
        for (var entry : pipeline) {
            var handler = entry.getValue();

            if (clazz.isInstance(handler))
                return entry.getKey();
        }

        return null;
    }

    public static void addFirstIfNotExists(ChannelPipeline pipeline, String name, ChannelInboundHandlerAdapter handler) {
        if (pipeline.get(name) == null)
            pipeline.addFirst(name, handler);
    }

    public static void addAfterIfNotExists(ChannelPipeline pipeline, String baseName, String name, ChannelInboundHandlerAdapter handler) {
        if (pipeline.get(name) == null)
            pipeline.addAfter(baseName, name, handler);
    }
}
