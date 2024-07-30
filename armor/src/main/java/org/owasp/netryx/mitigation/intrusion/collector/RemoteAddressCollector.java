package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.util.ChannelUtil;
import org.owasp.netryx.util.InetAddressUtil;

/**
 * First step in the intrusion detection process.
 * Collects the remote address and passes it to the next handler.
 */
public class RemoteAddressCollector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetector detector;
    private final IntrusionDetectionData collector;

    public RemoteAddressCollector(IntrusionDetector detector, IntrusionDetectionData collector) {
        this.detector = detector;
        this.collector = collector;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        collector.setRemoteAddress(InetAddressUtil.extractIp(ctx));

        ChannelUtil.handleChannelIntrusion(ctx, collector, detector, IntrusionPhase.CONNECT, 
                this::callSuperChannelActive);
    }

    private void callSuperChannelActive(ChannelHandlerContext ctx) {
        try {
            super.channelActive(ctx);
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }
    }
}
