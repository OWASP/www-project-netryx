package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.util.InetAddressUtil;

/**
 * First step in the intrusion detection process.
 * Collects the remote address and passes it to the next handler.
 */
public class RemoteAddressCollector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetectionData collector;

    public RemoteAddressCollector(IntrusionDetectionData collector) {
        this.collector = collector;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        collector.setRemoteAddress(InetAddressUtil.extractIp(ctx));
        super.channelActive(ctx);
    }
}
