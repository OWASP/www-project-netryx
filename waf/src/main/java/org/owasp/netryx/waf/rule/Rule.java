package org.owasp.netryx.waf.rule;

import lombok.Data;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.waf.condition.Condition;
import org.owasp.netryx.waf.constant.Action;

import java.util.ArrayList;
import java.util.List;

@Data
public class Rule implements Comparable<Rule> {
    private int priority = 0;
    private boolean enabled = true;
    private Action action = Action.BLOCK;
    private String ruleId = "";
    private String description = "";
    private IntrusionPhase phase = IntrusionPhase.CONNECT;

    private List<Condition> conditions = new ArrayList<>();

    @Override
    public int compareTo(Rule o) {
        return Integer.compare(priority, o.priority);
    }
}
