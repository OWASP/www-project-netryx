package org.owasp.netryx.waf.event;

import lombok.Builder;
import lombok.Data;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import org.owasp.netryx.waf.constant.Action;

@Data
@Builder
public class SecurityEvent implements Event {
    private Action action;
    private String ruleId;
    private IntrusionPhase phase;
    private String description;
    private IntrusionDetectionData data;
    private long timeStamp;

    public static SecurityEvent defaultEvent(Action action, IntrusionPhase phase, IntrusionDetectionData data) {
        return SecurityEvent.builder()
                .action(action)
                .ruleId("default")
                .phase(phase)
                .description("Default action triggered")
                .data(data)
                .timeStamp(System.currentTimeMillis())
                .build();
    }
}
