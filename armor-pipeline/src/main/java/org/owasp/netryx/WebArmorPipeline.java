package org.owasp.netryx;

import org.owasp.netryx.provider.NettyServerPipeline;

public interface WebArmorPipeline<T> {
    NettyServerPipeline<T> pipeline();
}
