package org.owasp.netryx.exception;

/**
 * ValidationException
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    private final String rule;
    private final String message;

    public ValidationException(String rule, String message) {
        this.rule = rule;
        this.message = message;
    }

    public String getRule() {
        return rule;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
