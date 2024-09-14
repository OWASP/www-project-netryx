package org.owasp.netryx.intrusion;

import org.owasp.netryx.mitigation.intrusion.constant.DetectCode;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;

/**
 * Represents the result of an intrusion detection.
 * <p>
 * Contains the detection code, detection data and the reason.
 */
public class DetectionResult {
    private final DetectCode code;
    private final IntrusionDetectionData data;
    private final String reason;

    public DetectionResult(DetectCode code, IntrusionDetectionData data, String reason) {
        this.code = code;
        this.data = data;
        this.reason = reason;
    }

    public DetectCode getCode() {
        return code;
    }

    public IntrusionDetectionData getData() {
        return data;
    }

    public String getReason() {
        return reason;
    }
}
