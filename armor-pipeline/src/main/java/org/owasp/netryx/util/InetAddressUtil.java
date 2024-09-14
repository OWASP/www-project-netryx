package org.owasp.netryx.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * InetAddressUtil
 * Utility class for extracting IP addresses from {@link ChannelHandlerContext}
 */
public final class InetAddressUtil {
    private InetAddressUtil() {}

    public static String extractIp(ChannelHandlerContext ctx) {
        var remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return remoteAddress.getAddress().getHostAddress();
    }
}
