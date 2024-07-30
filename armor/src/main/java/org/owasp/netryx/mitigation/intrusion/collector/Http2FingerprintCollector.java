package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http2.*;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.fingerprint.http2.PriorityFrame;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.util.ChannelUtil;

public class Http2FingerprintCollector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetector detector;
    private final IntrusionDetectionData collector;

    public Http2FingerprintCollector(IntrusionDetector detector, IntrusionDetectionData collector) {
        this.detector = detector;
        this.collector = collector;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2SettingsFrame)
            handleSettingsFrame(ctx, (Http2SettingsFrame) msg);
        else if (msg instanceof Http2WindowUpdateFrame)
            handleWindowUpdateFrame(ctx, (Http2WindowUpdateFrame) msg);
        else if (msg instanceof Http2PriorityFrame)
            handlePriorityFrame(ctx, (Http2PriorityFrame) msg);
        else if (msg instanceof Http2HeadersFrame)
            handleHeadersFrame(ctx, (Http2HeadersFrame) msg);
        else if (msg instanceof Http2DataFrame)
            handleDataFrame(ctx, (Http2DataFrame) msg);
        else
            super.channelRead(ctx, msg);
    }

    private void handleSettingsFrame(ChannelHandlerContext ctx, Http2SettingsFrame frame) throws Exception {
        processSettingsFrame(frame);
        super.channelRead(ctx, frame);
    }

    private void handleWindowUpdateFrame(ChannelHandlerContext ctx, Http2WindowUpdateFrame frame) throws Exception {
        processWindowUpdate(frame);
        super.channelRead(ctx, frame);
    }

    private void handlePriorityFrame(ChannelHandlerContext ctx, Http2PriorityFrame frame) throws Exception {
        processPriorityFrame(frame);
        super.channelRead(ctx, frame);
    }

    private void handleHeadersFrame(ChannelHandlerContext ctx, Http2HeadersFrame frame) throws Exception {
        processHeaders(frame);

        ChannelUtil.handleChannelIntrusion(ctx, collector, detector, IntrusionPhase.HEADERS,
                (context) -> channelRead0(context, frame));

        if (frame.isEndStream())
            ChannelUtil.handleChannelIntrusion(ctx, collector, detector, IntrusionPhase.HTTP2,
                    (context) -> channelRead0(context, frame));
        else
            super.channelRead(ctx, frame);
    }

    private void handleDataFrame(ChannelHandlerContext ctx, Http2DataFrame frame) throws Exception {
        if (frame.isEndStream())
            ChannelUtil.handleChannelIntrusion(ctx, collector, detector, IntrusionPhase.HTTP2,
                    (context) -> channelRead0(context, frame));
        else
            super.channelRead(ctx, frame);
    }

    private void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            super.channelRead(ctx, msg);
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }
    }

    private void processSettingsFrame(Http2SettingsFrame frame) {
        var fingerprintSettings = collector.getHttp2Fingerprint()
                .getSettings();

        frame.settings().forEach((c, l) -> fingerprintSettings.put((int) c, l));
    }

    private void processWindowUpdate(Http2WindowUpdateFrame update) {
        collector.getHttp2Fingerprint().setWindowUpdateValue(update.windowSizeIncrement());
    }

    private void processPriorityFrame(Http2PriorityFrame frame) {
        var streamId = frame.stream().id();
        var exclusiveBit = (byte) (frame.exclusive() ? 1 : 0);
        var dependentStreamId = frame.streamDependency();
        var weight = frame.weight();

        collector.getHttp2Fingerprint().addPriorityFrame(new PriorityFrame(
                streamId, exclusiveBit, dependentStreamId, weight
        ));
    }

    private void processHeaders(Http2HeadersFrame frame) {
        var headers = frame.headers();

        for (var header : headers) {
            var key = header.getKey().toString();

            if (key.startsWith(":"))
                collector.getHttp2Fingerprint().addPseudoHeader(key);
        }
    }
}
