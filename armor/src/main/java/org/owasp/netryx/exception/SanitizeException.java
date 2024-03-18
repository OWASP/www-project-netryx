package org.owasp.netryx.exception;

/**
 * SanitizeException
 * Exception thrown when sanitization fails.
 */
public class SanitizeException extends RuntimeException {
    public SanitizeException(String message) {
        super(message);
    }
}
