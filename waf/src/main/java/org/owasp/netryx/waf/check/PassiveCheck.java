package org.owasp.netryx.waf.check;

import org.owasp.netryx.constant.IntrusionPhase;

public interface PassiveCheck {
    IntrusionPhase getPhase();
}
