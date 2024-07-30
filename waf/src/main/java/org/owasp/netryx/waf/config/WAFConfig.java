package org.owasp.netryx.waf.config;

import lombok.Data;
import org.owasp.netryx.waf.constant.Action;
import org.owasp.netryx.waf.constant.PassiveCheck;
import org.owasp.netryx.waf.rule.RuleEngine;

import java.util.Set;

@Data
public class WAFConfig {
    private Action defaultAction;
    private Set<PassiveCheck> disabledChecks;
    private RuleEngine engine;
}
