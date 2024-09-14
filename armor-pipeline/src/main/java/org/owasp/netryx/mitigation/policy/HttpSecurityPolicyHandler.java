package org.owasp.netryx.mitigation.policy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.constant.ArmorConstant;
import org.owasp.netryx.constant.ChannelScope;
import org.owasp.netryx.mitigation.OutboundMitigationHandler;
import org.owasp.netryx.provider.NettyServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpSecurityHandler
 * Applies security policies to HTTP responses
 *
 * @see org.owasp.netryx.policy.SecurityPolicy
 */
@ChannelHandler.Sharable
public class HttpSecurityPolicyHandler extends OutboundMitigationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSecurityPolicyHandler.class);

    private static HttpSecurityPolicyHandler instance;

    public static synchronized HttpSecurityPolicyHandler getInstance(SecurityConfig config) {
        if (instance == null)
            instance = new HttpSecurityPolicyHandler(config);

        return instance;
    }

    private final SecurityConfig config;

    private HttpSecurityPolicyHandler(SecurityConfig config) {
        this.config = config;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpResponse)) {
            super.write(ctx, msg, promise);
            return;
        }
        var response = (HttpResponse) msg;

        var policies = config.policies();

        LOGGER.debug("Applying {} security policies to HTTP response", policies.size());

        for (var policy : policies)
            policy.apply((name, value) -> response.headers().set(name, value));

        super.write(ctx, response, promise);
    }

    @Override
    public void apply(ChannelScope scope, NettyServerProvider<?> server, ChannelPipeline pipeline) {
        if (scope == ChannelScope.CONNECTION)
            server.addLast(pipeline, ArmorConstant.HTTP_SECURITY_POLICY_HANDLER, this);
    }
}
