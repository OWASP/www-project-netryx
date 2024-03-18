package org.owasp.netryx.reactor;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.ApplicationProtocolNames;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.provider.NettyServerPipeline;
import org.owasp.netryx.provider.NettyServerProvider;
import org.owasp.netryx.reactor.alpn.AlpnPipelineAdapter;
import reactor.netty.NettyPipeline;
import reactor.netty.http.server.HttpServer;

import java.util.function.BiConsumer;

/**
 * ReactorNettyProvider
 * Implements NettyServer Pipeline for Reactor Netty
 */
public class ReactorNettyProvider implements NettyServerProvider<HttpServer> {
    private final SecurityConfig config;

    public ReactorNettyProvider(SecurityConfig config) {
        this.config = config;
    }

    /**
     * Adds a ChannelHandler to the first position of the ChannelPipeline.
     * Intended to be used for adding ChannelHandlers in doOnChannelInit.
     * <p>
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addFirst(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        pipeline.addFirst(name, handler);
    }

    /**
     * Adds a ChannelHandler before the Http1 Request Handler or the Http2 Handler.
     * At this stage, the HttpContent is not yet decoded.
     * <p>
     * Intended to be used in doOnChannelInit.
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addBeforeHttpRequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        var alpn = alpnAdapter(pipeline);

        BiConsumer<ChannelPipeline, String> addHandlerLogic = (p, protocol) -> {
            if (protocol.equals(ApplicationProtocolNames.HTTP_2)) {
                p.addBefore(NettyPipeline.H2MultiplexHandler, name, handler);
            } else {
                addBeforeHttp1RequestHandler(p, name, handler);
            }
        };

        if (alpn == null) {
            var protocolName = pipeline.get(NettyPipeline.H2MultiplexHandler) != null ? ApplicationProtocolNames.HTTP_2
                    : ApplicationProtocolNames.HTTP_1_1;

            addHandlerLogic.accept(pipeline, protocolName);
        }
        else
            alpn.addConditionalAdapter(addHandlerLogic::accept);
    }

    /**
     * Adds a ChannelHandler before the Http1 Request Handler.
     * At this stage, the HttpContent is not yet decoded.
     * <p>
     * Intended to be used in doOnChannelInit.
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addBeforeHttp1RequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        if (pipeline.get(name) != null)
            return;

        if (pipeline.get(NettyPipeline.HttpTrafficHandler) != null)
            pipeline.addBefore(NettyPipeline.HttpTrafficHandler, name, handler);
    }

    /**
     * Adds a ChannelHandler before the Http2 Handler.
     * At this stage, HTTP/2 frames can be intercepted.
     * <p>
     * Intended to be used in doOnChannelInit
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addBeforeHttp2Handler(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        if (pipeline.get(name) != null)
            return;

        var http2Handler = pipeline.get(NettyPipeline.H2MultiplexHandler);

        if (http2Handler != null) {
            pipeline.addBefore(NettyPipeline.H2MultiplexHandler, name, handler);
            return;
        }

        var alpn = alpnAdapter(pipeline);

        if (alpn == null)
            return;

        alpn.addConditionalAdapter((p, protocol) -> {
            if (!protocol.equals(ApplicationProtocolNames.HTTP_2))
                return;

            p.addBefore(NettyPipeline.H2MultiplexHandler, name, handler);
        });
    }

    /**
     * Adds a ChannelHandler before the SslHandler.
     * At this stage, raw TLS packets can be intercepted (e.g. ClientHello).
     * <p>
     * Intended to be used in doOnChannelInit
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addBeforeSslHandler(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        if (pipeline.get(name) != null)
            return;

        var sslHandler = pipeline.get(NettyPipeline.SslHandler);

        if (sslHandler != null)
            pipeline.addBefore(NettyPipeline.SslHandler, name, handler);
    }

    /**
     * Adds a ChannelHandler before the HttpTrafficHandler.
     * At this stage, HTTP requests can be intercepted.
     * Note, that the HttpRequests provided are already full (content included).
     * <p>
     * If you need to handle them before the content is decoded, use addBeforeHttp1RequestHandler
     * or addBeforeHttp2Handler.
     * <p>
     * Intended to be used in doOnConnection
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addAfterHttpTrafficHandler(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        if (pipeline.get(name) != null)
            return;

        var httpTrafficHandler = pipeline.get(NettyPipeline.HttpTrafficHandler);

        // If HttpAggregator is not present, add it.
        // It is required to properly handle HTTP/1.1 requests.

        if (httpTrafficHandler != null)
            pipeline.addAfter(NettyPipeline.HttpTrafficHandler, name, handler);

        var aggregator = pipeline.get(NettyPipeline.HttpAggregator);
        var isHttp2 = pipeline.get(NettyPipeline.H2ToHttp11Codec) != null;

        if (aggregator == null && !isHttp2)
            pipeline.addAfter(NettyPipeline.HttpTrafficHandler, NettyPipeline.HttpAggregator,
                    new HttpObjectAggregator(config.http1Settings().getMaxObjectSize()));
    }

    /**
     * Adds a ChannelHandler to the last position of the ChannelPipeline.
     * Intended to be used for adding ChannelHandlers in doOnConnection.
     * <p>
     * For instance, HttpResponse handlers should be added here.
     * @param pipeline - ChannelPipeline
     * @param name - Name of the ChannelHandler
     * @param handler - ChannelHandler
     */
    @Override
    public void addLast(ChannelPipeline pipeline, String name, ChannelHandler handler) {
        if (pipeline.get(name) != null)
            return;

        pipeline.addLast(name, handler);
    }

    private AlpnPipelineAdapter alpnAdapter(ChannelPipeline pipeline) {
        var adapter = pipeline.get(AlpnPipelineAdapter.NAME);

        if (adapter != null)
            return (AlpnPipelineAdapter) adapter;

        var h2orh1 = pipeline.get(NettyPipeline.H2OrHttp11Codec);

        if (h2orh1 == null)
            return null;

        var pipelineAdapter = new AlpnPipelineAdapter();
        pipeline.addAfter(NettyPipeline.H2OrHttp11Codec, AlpnPipelineAdapter.NAME, pipelineAdapter);

        return pipelineAdapter;
    }

    @Override
    public NettyServerPipeline<HttpServer> newPipeline() {
        return new ReactorNettyPipeline(this);
    }
}
