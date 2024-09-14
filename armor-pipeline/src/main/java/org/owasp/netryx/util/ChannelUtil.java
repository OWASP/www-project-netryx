package org.owasp.netryx.util;

import io.netty.channel.ChannelHandlerContext;
import org.owasp.netryx.constant.HandleCode;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.intrusion.DetectionResult;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.intrusion.constant.DetectCode;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

public class ChannelUtil {
    private ChannelUtil() {}

    public static void handleChannelIntrusion(
        ChannelHandlerContext ctx, IntrusionDetectionData collector,
        IntrusionDetector detector, IntrusionPhase phase,
        Consumer<ChannelHandlerContext> actionHandler
    ) {
        detector.detect(phase, collector)
                .flatMap(result -> {
                    if (result.getCode() == DetectCode.OK) {
                        return Mono.just(HandleCode.PROCEED);
                    } else {
                        return handleDetection(result, detector);
                    }
                })
                .onErrorResume(t -> {
                    ctx.fireExceptionCaught(t);
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.just(HandleCode.PROCEED))
                .subscribeOn(Schedulers.fromExecutor(ctx.executor()))
                .subscribe(
                        handleCode -> processHandleCode(ctx, handleCode, actionHandler),
                        ctx::fireExceptionCaught
                );
    }

    private static Mono<HandleCode> handleDetection(
        DetectionResult result,
        IntrusionDetector detector
    ) {
        return detector.onDetected(result);
    }

    private static void processHandleCode(ChannelHandlerContext ctx, HandleCode handleCode, Consumer<ChannelHandlerContext> handler) {
        if (handleCode == HandleCode.PROCEED) {
            try {
                handler.accept(ctx);
            } catch (Exception e) {
                ctx.fireExceptionCaught(e);
            }
        } else {
            ctx.close();
        }
    }
}