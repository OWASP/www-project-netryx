package org.owasp.netryx.constant;

/**
 * Represents the handle code after detection
 */
public enum HandleCode {
    // PROCEED means that the request is allowed to proceed
    PROCEED,
    // BLOCK means that the request is blocked. The channel will be closed.
    BLOCK
}
