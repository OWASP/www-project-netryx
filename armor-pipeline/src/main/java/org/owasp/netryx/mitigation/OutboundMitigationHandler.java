package org.owasp.netryx.mitigation;

import io.netty.channel.ChannelOutboundHandlerAdapter;

public abstract class OutboundMitigationHandler extends ChannelOutboundHandlerAdapter implements MitigationHandler {
    @Override
    public void close() {}
}
