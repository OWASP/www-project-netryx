package org.owasp.netryx.mitigation.intrusion;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.owasp.netryx.intrusion.DetectionResult;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.intrusion.constant.DetectCode;
import org.owasp.netryx.constant.HandleCode;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class IntrusionChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntrusionChannelHandler.class);

    private final IntrusionDetectionData collector;
    private final IntrusionDetector detector;

    public IntrusionChannelHandler(IntrusionDetectionData collector, IntrusionDetector detector) {
        this.collector = collector;
        this.detector = detector;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            var request = (FullHttpRequest) msg;

            handleFullHttpRequest(ctx, request);
            return;
        }

        super.channelRead(ctx, msg);
    }

    private void handleFullHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        LOGGER.debug("Collecting data for request {}", request);

        collector.setRequest(request);
        handleIntrusionDetection(ctx, request, collector);
    }

    // Handling reactive detection with subscribing on netty event executor for thread-safety
    private void handleIntrusionDetection(ChannelHandlerContext ctx, Object msg, IntrusionDetectionData data) {
        detector.detect(data)
                .flatMap(this::processDetectionResult)
                .subscribeOn(Schedulers.fromExecutor(ctx.executor()))
                .subscribe(
                    handleCode -> processHandleCode(ctx, msg, handleCode),
                    ctx::fireExceptionCaught
                );
    }

    private Mono<HandleCode> processDetectionResult(DetectionResult result) {
        if (result.getCode() == DetectCode.OK) {
            return Mono.just(HandleCode.PROCEED);
        } else {
            return detector.onDetected(result);
        }
    }

    private void processHandleCode(ChannelHandlerContext ctx, Object msg, HandleCode handleCode) {
        if (handleCode == HandleCode.PROCEED) {
            try {
                super.channelRead(ctx, msg);
            } catch (Exception e) {
                ctx.fireExceptionCaught(e);
            }
        } else {
            LOGGER.debug("Intrusion detected, closing channel");
            ctx.close();
        }
    }
}
