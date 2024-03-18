package org.owasp.netryx.reactor.alpn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

import java.util.ArrayList;
import java.util.List;

public class AlpnPipelineAdapter extends ApplicationProtocolNegotiationHandler {
    public static final String NAME = "netarmor.AlpnPipelineAdapter";

    private final List<ProtocolBasedAdapter> adapters = new ArrayList<>();

    public AlpnPipelineAdapter() {
        super(ApplicationProtocolNames.HTTP_1_1);
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
        var pipeline = ctx.pipeline();

        for (var adapter : adapters)
            adapter.add(pipeline, protocol);
    }

    public void addConditionalAdapter(ProtocolBasedAdapter adapter) {
        adapters.add(adapter);
    }
}
