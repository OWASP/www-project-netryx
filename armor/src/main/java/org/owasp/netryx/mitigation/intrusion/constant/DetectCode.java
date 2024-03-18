package org.owasp.netryx.mitigation.intrusion.constant;

/**
 * Represents the result code of an intrusion detection.
 */
public enum DetectCode {
    // OK means that the request is not suspicious
    OK,
    // SUSPICIOUS means that the request is suspicious, but not malicious
    SUSPICIOUS,
    // MALICIOUS means that the request is malicious
    MALICIOUS
}
