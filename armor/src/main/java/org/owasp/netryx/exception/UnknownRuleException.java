package org.owasp.netryx.exception;

/**
 * UnknownPatternException
 * Exception thrown when an unknown rule for input validation is requested.
 *
 * @see org.owasp.netryx.validator.InputValidator
 *
 * NOTE: Using the rule means that the developer is aware of it and throwing an exception
 * is a better alternative than not validating input at all to prevent unexpected behavior.
 */
public class UnknownRuleException extends RuntimeException {
    private final String name;

    public UnknownRuleException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
