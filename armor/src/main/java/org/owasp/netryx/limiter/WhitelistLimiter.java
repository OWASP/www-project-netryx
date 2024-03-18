package org.owasp.netryx.limiter;

import reactor.core.publisher.Mono;

public interface WhitelistLimiter {
    Mono<Boolean> isAllowed(String address);

    // Handler name. Should be unique.
    String name();
}
