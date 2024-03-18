package org.owasp.netryx.pipeline;

import org.owasp.netryx.provider.NettyServerPipeline;

public interface WebArmorPipeline<T> {
    NettyServerPipeline<T> pipeline();
}
