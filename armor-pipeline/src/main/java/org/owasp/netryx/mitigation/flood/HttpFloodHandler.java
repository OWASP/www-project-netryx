package org.owasp.netryx.mitigation.flood;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.*;
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
 * HttpFloodHandler
 * Limits the amount of requests per IP address.
 * <p>
 * Works both for HTTP/1.1 and HTTP/2.
 */
@ChannelHandler.Sharable
public class HttpFloodHandler extends InboundMitigationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFloodHandler.class);

    private static HttpFloodHandler instance;

    public static HttpFloodHandler getInstance(SecurityConfig config) {
        if (instance == null)
            instance = new HttpFloodHandler(config);

        return instance;
    }

    private final AtomicAddressLimiter addressLimiter;

    private HttpFloodHandler(SecurityConfig config) {
        this.addressLimiter = new AtomicAddressLimiter(config.requestLimiterConfig());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            var frame = (Http2HeadersFrame) msg;

            if (frame.isEndStream() && blockRequest(ctx)) {
                tooManyRequestsH2(frame.stream(), ctx);
                return;
            }
        }

        if (msg instanceof Http2DataFrame) {
            var frame = (Http2DataFrame) msg;

            if (frame.isEndStream() && blockRequest(ctx)) {
                tooManyRequestsH2(frame.stream(), ctx);
                return;
            }
        }

        if (msg instanceof HttpRequest && blockRequest(ctx)) {
            tooManyRequests(ctx);
            return;
        }

        super.channelRead(ctx, msg);
    }

    private boolean blockRequest(ChannelHandlerContext ctx) {
        var address = InetAddressUtil.extractIp(ctx);
        addressLimiter.increment(address);

        return addressLimiter.isBadAddress(address);
    }

    private void tooManyRequests(ChannelHandlerContext ctx) {
        LOGGER.debug("HTTP/1.1 Request Flood detected by {}", InetAddressUtil.extractIp(ctx));

        var response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.TOO_MANY_REQUESTS);

        ctx.writeAndFlush(response).addListener(future -> ctx.close());
    }

    private void tooManyRequestsH2(Http2FrameStream stream, ChannelHandlerContext ctx) {
        LOGGER.debug("HTTP/2 Request Flood detected by {}", InetAddressUtil.extractIp(ctx));

        var headers = new DefaultHttp2Headers()
                .status(HttpResponseStatus.TOO_MANY_REQUESTS.codeAsText());

        var frame = new DefaultHttp2HeadersFrame(headers, true)
                .stream(stream);

        ctx.writeAndFlush(frame).addListener(future -> ctx.close());
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        if (scope == ChannelScope.INIT)
            server.addBeforeHttpRequestHandler(pipeline, ArmorConstant.HTTP_FLOOD_HANDLER, this);
    }

    @Override
    public void close() {
        addressLimiter.close();
    }
}
