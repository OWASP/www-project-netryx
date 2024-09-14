package org.owasp.netryx.mitigation.flood;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http2.Http2ResetFrame;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.constant.ArmorConstant;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.mitigation.InboundMitigationHandler;
import org.owasp.netryx.provider.NettyServerProvider;
import org.owasp.netryx.util.InetAddressUtil;
import org.owasp.netryx.util.limiter.AtomicAddressLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RapidResetHandler
 * Limits the amount of HTTP/2 reset frames per IP address
 * <p>
 * Fix for HTTP/2 RST flood vulnerability
 */
@ChannelHandler.Sharable
public class RapidResetHandler extends InboundMitigationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RapidResetHandler.class);

    private static RapidResetHandler instance;

    public static synchronized RapidResetHandler getInstance(SecurityConfig config) {
        if (instance == null)
            instance = new RapidResetHandler(config);

        return instance;
    }

    private final AtomicAddressLimiter addressLimiter;

    private RapidResetHandler(SecurityConfig config) {
        this.addressLimiter = new AtomicAddressLimiter(config.rapidResetConfig());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (addressLimiter.isBadAddress(ctx)) {
            LOGGER.debug("Disallowed address {} connected due to Rapid Reset", InetAddressUtil.extractIp(ctx));
            ctx.close();
            return;
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2ResetFrame) {
            var address = InetAddressUtil.extractIp(ctx);
            addressLimiter.increment(address);

            if (addressLimiter.isBadAddress(address)) {
                block(ctx);
                return;
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        if (scope == ChannelScope.INIT)
            server.addBeforeHttp2Handler(pipeline, ArmorConstant.RAPID_RESET_HANDLER, this);
    }

    private void block(ChannelHandlerContext ctx) {
        LOGGER.debug("Rapid Reset abuse detected by {}", InetAddressUtil.extractIp(ctx));
        ctx.close();
    }

    @Override
    public void close() {
        addressLimiter.close();
    }
}