package org.owasp.netryx.waf.condition;

import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import reactor.core.publisher.Mono;

public interface Condition {
    Mono<Boolean> matches(IntrusionDetectionData data);
}
