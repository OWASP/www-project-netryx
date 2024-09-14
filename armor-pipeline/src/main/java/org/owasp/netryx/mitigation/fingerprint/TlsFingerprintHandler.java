package org.owasp.netryx.mitigation.fingerprint;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.fingerprint.constant.ResultCode;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;
import org.owasp.netryx.mitigation.InboundMitigationHandler;
import org.owasp.netryx.provider.NettyServerProvider;
import org.owasp.netryx.util.io.UIntDataInputStream;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

/**
 * TlsFingerprintHandler
 * Handles TLS fingerprint packets
 * <p>
 * TLS Fingerprinting is a great technique to detect and block malicious traffic
 * or allowing specific traffic to pass through (e.g. mobile traffic)
 * <p>
 * It is hard to spoof TLS fingerprints for malicious purposes,
 * requiring a lot of resources and time.
 * <p>
 * NOTE: You should follow the privacy policy while using this feature.
 * Don't rely on this feature only, it is not a silver bullet.
 */
public class TlsFingerprintHandler extends InboundMitigationHandler {
    private final TlsFingerprintPacketHandler handler;

    public TlsFingerprintHandler(TlsFingerprintPacketHandler handle) {
        this.handler = handle;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (handler == null) return;

        if (!(msg instanceof ByteBuf)) {
            super.channelRead(ctx, msg);
            return;
        }

        var buf = (ByteBuf) msg;

        var bytes = readBytes(buf);

        if (!ClientHello.isClientHello(bytes)) {
            super.channelRead(ctx, msg);
            return;
        }

        process(ctx, msg, bytes);
    }

    private void process(ChannelHandlerContext ctx, Object msg, byte[] bytes) throws IOException {
        try (var in = new UIntDataInputStream(bytes)) {
            var clientHello = new ClientHello(in);

            handler.handle(ctx, clientHello)
                    .subscribeOn(Schedulers.fromExecutor(ctx.executor()))
                    .subscribe(resultCode -> handleResultCode(ctx, msg, resultCode));
        }
    }

    private void handleResultCode(ChannelHandlerContext ctx, Object msg, ResultCode resultCode) {
        if (resultCode == ResultCode.BLOCK)
            ctx.close();
        else {
            try {
                super.channelRead(ctx, msg);
            } catch (Exception e) {
                ctx.fireExceptionCaught(e);
            }
        }
    }

    private static byte[] readBytes(ByteBuf buf) {
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return bytes;
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        if (scope == ChannelScope.INIT)
            server.addBeforeSslHandler(pipeline, handler.name(), this);
    }
}
