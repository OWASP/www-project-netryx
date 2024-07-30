package org.owasp.netryx.waf.rule;

import reactor.core.publisher.Flux;

public interface RuleEngine {
    Flux<Rule> fetchRules();
}
