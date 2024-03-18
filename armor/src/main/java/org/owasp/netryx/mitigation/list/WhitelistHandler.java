package org.owasp.netryx.mitigation.list;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.limiter.WhitelistLimiter;
import org.owasp.netryx.mitigation.InboundMitigationHandler;
import org.owasp.netryx.provider.NettyServerProvider;
import org.owasp.netryx.util.InetAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

/**
 * A {@link ChannelInboundHandlerAdapter} that closes the connection if the remote address is not whitelisted.
 */
public class WhitelistHandler extends InboundMitigationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistHandler.class);

    private final WhitelistLimiter limiter;

    public WhitelistHandler(WhitelistLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        var remoteAddress = InetAddressUtil.extractIp(ctx);

        limiter.isAllowed(remoteAddress)
                .subscribeOn(Schedulers.fromExecutor(ctx.executor()))
                .subscribe(allowed -> handleIfAllowed(allowed, ctx));
    }

    private void handleIfAllowed(boolean allowed, ChannelHandlerContext ctx) {
        if (!allowed) {
            LOGGER.debug("Closing connection because remote address {} is not whitelisted",
                    InetAddressUtil.extractIp(ctx));
            ctx.close();
        } else {
            try {
                super.channelActive(ctx);
            } catch (Exception e) {
                ctx.fireExceptionCaught(e);
            }
        }
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        if (scope == ChannelScope.INIT)
            server.addFirst(pipeline, limiter.name(), this);
    }
}
