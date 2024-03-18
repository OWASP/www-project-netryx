package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.util.BytesUtil;
import org.owasp.netryx.util.io.UIntDataInputStream;

/**
 * Second step in the intrusion detection process.
 * Collects the TLS fingerprint and passes it to the next handler.
 */
public class TlsFingerprintCollector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetectionData collector;

    public TlsFingerprintCollector(IntrusionDetectionData collector) {
        this.collector = collector;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            super.channelRead(ctx, msg);
            return;
        }

        var buf = (ByteBuf) msg;

        var bufCopy = buf.copy();
        var bytes = BytesUtil.readBytes(bufCopy);

        bufCopy.release();

        if (ClientHello.isClientHello(bytes)) {
            try (var in = new UIntDataInputStream(bytes)) {
                var clientHello = new ClientHello(in);
                collector.setClientHello(clientHello);
            }
        }

        super.channelRead(ctx, msg);
    }
}
