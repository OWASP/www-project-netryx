package org.owasp.netryx.reactor;

import io.netty.channel.Channel;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.mitigation.MitigationHandler;
import org.owasp.netryx.provider.NettyServerPipeline;
import reactor.netty.http.server.HttpServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * NetArmorConfigurer
 * Configures the NetArmor pipeline.
 */
public class ReactorNettyPipeline implements NettyServerPipeline<HttpServer> {
    private final ReactorNettyProvider server;

    private final List<Supplier<MitigationHandler>> mitigationHandlers = new ArrayList<>();

    // Context handlers holder.
    private final Map<String, List<MitigationHandler>> contextHandlers = new ConcurrentHashMap<>();

    public ReactorNettyPipeline(ReactorNettyProvider server) {
        this.server = server;
    }

    @Override
    public void addMitigationHandler(Supplier<MitigationHandler> mitigationHandler) {
        mitigationHandlers.add(mitigationHandler);
    }

    @Override
    public HttpServer configure(HttpServer bootstrap) {
        return bootstrap.doOnChannelInit((observer, channel, remote) -> {
                    initChannel(channel);
                    releaseHandlersOnClose(channel);
                })
                .doOnConnection(con -> initConnection(con.channel()));
    }

    private void initChannel(Channel ch) {
        var id = ch.id().asShortText();
        var handlers = computeHandlers(id);

        for (var handler : handlers)
            handler.apply(ChannelScope.INIT, server, ch.pipeline());
    }

    private void initConnection(Channel ch) {
        // Extracting id. For HTTP/1.1 it is the same as channel id
        // but for HTTP/2 it contains stream id.
        var id = extractId(ch.id().asShortText());
        var handlers = contextHandlers.get(id);

        if (handlers != null)
            for (var handler : handlers)
                handler.apply(ChannelScope.CONNECTION, server, ch.pipeline());
    }

    private void releaseHandlersOnClose(Channel ch) {
        ch.closeFuture().addListener((v) -> contextHandlers.remove(ch.id().asShortText()));
    }

    private List<MitigationHandler> computeHandlers(String id) {
        return contextHandlers.computeIfAbsent(id, k -> mitigationHandlers.stream()
                .map(Supplier::get)
                .collect(Collectors.toList()));
    }

    private String extractId(String id) {
        return id.split("/")[0];
    }
}
