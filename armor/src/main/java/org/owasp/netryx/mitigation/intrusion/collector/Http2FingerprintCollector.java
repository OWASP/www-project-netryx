package org.owasp.netryx.mitigation.intrusion.collector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2PriorityFrame;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.handler.codec.http2.Http2WindowUpdateFrame;
import org.owasp.netryx.fingerprint.http2.PriorityFrame;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;

public class Http2FingerprintCollector extends ChannelInboundHandlerAdapter {
    private final IntrusionDetectionData collector;

    public Http2FingerprintCollector(IntrusionDetectionData collector) {
        this.collector = collector;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2SettingsFrame)
            processSettingsFrame((Http2SettingsFrame) msg);

        if (msg instanceof Http2WindowUpdateFrame)
            processWindowUpdate((Http2WindowUpdateFrame) msg);

        if (msg instanceof Http2PriorityFrame)
            processPriorityFrame((Http2PriorityFrame) msg);

        if (msg instanceof Http2HeadersFrame)
            processHeaders((Http2HeadersFrame) msg);

        super.channelRead(ctx, msg);
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
