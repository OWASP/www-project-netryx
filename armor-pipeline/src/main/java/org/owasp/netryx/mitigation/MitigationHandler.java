package org.owasp.netryx.mitigation;

import io.netty.channel.ChannelPipeline;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.provider.NettyServerProvider;

import java.io.Closeable;

public interface MitigationHandler extends Closeable {
    void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline);
}
