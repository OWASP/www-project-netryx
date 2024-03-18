package org.owasp.netryx.mitigation.intrusion;

import io.netty.channel.ChannelPipeline;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.MitigationHandler;
import org.owasp.netryx.mitigation.intrusion.collector.Http2FingerprintCollector;
import org.owasp.netryx.mitigation.intrusion.collector.RemoteAddressCollector;
import org.owasp.netryx.mitigation.intrusion.collector.TlsFingerprintCollector;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.provider.NettyServerProvider;

import java.io.IOException;

import static org.owasp.netryx.mitigation.intrusion.constant.HandlerName.*;

/**
 * IntrusionMitigationHandler
 * Configures the intrusion detection pipeline.
 */
public class IntrusionMitigationHandler implements MitigationHandler {
    /**
     * Shared collector for all connections in this channel.
     * While it is ok to have shared collector for HTTP/1.1,
     * HTTP/2 requires to have separate collector for each possible stream.
     */
    private final IntrusionDetectionData sharedCollector = new IntrusionDetectionData();

    private final IntrusionDetector detector;

    public IntrusionMitigationHandler(IntrusionDetector detector) {
        this.detector = detector;
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        switch (scope) {
            case INIT: {
                configureChannel(server, pipeline);
                break;
            }
            case CONNECTION: {
                configureConnection(server, pipeline);
            }
        }
    }

    @Override
    public void close() throws IOException {}

    private void configureChannel(NettyServerProvider<?> server, ChannelPipeline pipeline) {
        server.addFirst(pipeline, REMOTE_ADDRESS, new RemoteAddressCollector(sharedCollector));
        server.addBeforeSslHandler(pipeline, TLS_FINGERPRINT, new TlsFingerprintCollector(sharedCollector));
        server.addBeforeHttp2Handler(pipeline, HTTP2_FINGERPRINT, new Http2FingerprintCollector(sharedCollector));
    }

    private void configureConnection(NettyServerProvider<?> server, ChannelPipeline pipeline) {
        // We create new collector for each connection. No need in HTTP/1.1,
        // but required for HTTP/2 multiplexing.
        var streamDetector = new IntrusionDetectionData(sharedCollector);

        server.addAfterHttpTrafficHandler(pipeline, detector.name(),
                new IntrusionChannelHandler(streamDetector, detector));
    }
}
