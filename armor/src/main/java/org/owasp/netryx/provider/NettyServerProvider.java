package org.owasp.netryx.provider;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public interface NettyServerProvider<T> {
    void addFirst(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttpRequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttp1RequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttp2Handler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeSslHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addAfterHttpTrafficHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addLast(ChannelPipeline pipeline, String name, ChannelHandler handler);

    NettyServerPipeline<T> newPipeline();
}
