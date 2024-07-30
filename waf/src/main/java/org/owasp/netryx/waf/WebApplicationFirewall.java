package org.owasp.netryx.waf;

import org.owasp.netryx.constant.HandleCode;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.events.manager.EventScope;
import org.owasp.netryx.intrusion.DetectionResult;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.mitigation.intrusion.constant.DetectCode;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.waf.config.WAFConfig;
import org.owasp.netryx.waf.event.SecurityEvent;
import org.owasp.netryx.waf.rule.Rule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebApplicationFirewall implements IntrusionDetector {
    private static final String NAME = "netryx-waf";

    private final WAFConfig config;
    private final EventScope eventScope;

    public WebApplicationFirewall(WAFConfig config, EventScope eventScope) {
        this.config = config;
        this.eventScope = eventScope;
    }

    @Override
    public Mono<DetectionResult> detect(IntrusionPhase phase, IntrusionDetectionData data) {
        return getSortedRulesForPhase(phase)
                .flatMap(rule -> checkRuleConditions(rule, data)
                        .filter(matches -> matches)
                        .flatMap(matches -> executeRuleAction(rule, phase, data)))
                .next()
                .switchIfEmpty(getDefaultDetectionResult(phase, data));
    }

    private Flux<Rule> getSortedRulesForPhase(IntrusionPhase phase) {
        return config.getEngine().fetchRules()
                .filter(rule -> rule.getPhase() == phase && rule.isEnabled())
                .sort();
    }

    private Mono<Boolean> checkRuleConditions(Rule rule, IntrusionDetectionData data) {
        return Flux.fromIterable(rule.getConditions())
                .flatMap(condition -> condition.matches(data))
                .all(match -> match);
    }

    private Mono<DetectionResult> executeRuleAction(Rule rule, IntrusionPhase phase, IntrusionDetectionData data) {
        var event = SecurityEvent.builder()
                .action(rule.getAction())
                .ruleId(rule.getRuleId())
                .phase(phase)
                .description(rule.getDescription())
                .data(data)
                .timeStamp(System.currentTimeMillis())
                .build();

        switch (rule.getAction()) {
            case ALLOW:
                return Mono.just(new DetectionResult(DetectCode.OK, data, rule.getDescription()));
            case BLOCK:
                return Mono.fromFuture(eventScope.callAsync(event))
                        .then(Mono.just(new DetectionResult(DetectCode.MALICIOUS, data, rule.getDescription())));
            case MONITOR:
                return Mono.fromFuture(eventScope.callAsync(event))
                        .then(Mono.just(new DetectionResult(DetectCode.OK, data, rule.getDescription())));
            default:
                return Mono.empty();
        }
    }

    private Mono<DetectionResult> getDefaultDetectionResult(IntrusionPhase phase, IntrusionDetectionData data) {
        var defaultAction = config.getDefaultAction();

        switch (defaultAction) {
            case ALLOW:
                return Mono.just(new DetectionResult(DetectCode.OK, data, null));
            case BLOCK:
                return Mono.fromFuture(eventScope.callAsync(SecurityEvent.defaultEvent(defaultAction, phase, data)))
                        .then(Mono.just(new DetectionResult(DetectCode.MALICIOUS, data, null)));
            default:
                return Mono.fromFuture(eventScope.callAsync(SecurityEvent.defaultEvent(defaultAction, phase, data)))
                        .then(Mono.just(new DetectionResult(DetectCode.OK, data, null)));
        }
    }

    @Override
    public Mono<HandleCode> onDetected(DetectionResult result) {
        if (result.getCode() == DetectCode.SUSPICIOUS || result.getCode() == DetectCode.MALICIOUS)
            return Mono.just(HandleCode.BLOCK);

        return Mono.just(HandleCode.PROCEED);
    }

    @Override
    public String name() {
        return NAME;
    }
}
