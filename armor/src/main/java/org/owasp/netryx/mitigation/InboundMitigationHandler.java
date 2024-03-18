package org.owasp.netryx.mitigation;

import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class InboundMitigationHandler extends ChannelInboundHandlerAdapter implements MitigationHandler {
    @Override
    public void close() {}
}
