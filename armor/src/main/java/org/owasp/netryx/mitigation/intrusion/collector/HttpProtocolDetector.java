package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http2.Http2Frame;
import org.owasp.netryx.constant.HttpProtocol;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;

public class HttpProtocolDetector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetectionData collector;

    public HttpProtocolDetector(IntrusionDetectionData collector) {
        this.collector = collector;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2Frame)
            collector.setHttpProtocol(HttpProtocol.HTTP_2_0);

        super.channelRead(ctx, msg);
    }
}
