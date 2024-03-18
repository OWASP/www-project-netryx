package org.owasp.netryx.reactor.alpn;

import io.netty.channel.ChannelPipeline;

@FunctionalInterface
public interface ProtocolBasedAdapter {
    void add(ChannelPipeline pipeline, String protocol);
}
