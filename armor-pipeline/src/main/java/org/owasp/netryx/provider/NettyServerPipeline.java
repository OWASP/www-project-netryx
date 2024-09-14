package org.owasp.netryx.provider;

import org.owasp.netryx.mitigation.MitigationHandler;

import java.util.function.Supplier;

public interface NettyServerPipeline<T> {
    void addMitigationHandler(Supplier<MitigationHandler> mitigationHandler);

    T configure(T bootstrap);
}
