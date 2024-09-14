package org.owasp.netryx.limiter;

import reactor.core.publisher.Mono;

public interface BlacklistLimiter {
    Mono<Boolean> isBlocked(String address);

    // Handler name. Should be unique.
    String name();
}
