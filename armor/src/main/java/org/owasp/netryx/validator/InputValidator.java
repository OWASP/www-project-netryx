package org.owasp.netryx.validator;

import java.util.concurrent.CompletableFuture;

/**
 * InputValidator
 * Input validator interface.
 * <p>
 * Validation is one of the most important parts of the application.
 * Improper validation can lead to security vulnerabilities.
 * <p>
 * Every validation pattern can be configured in the configuration file.
 * @see org.owasp.netryx.validator.impl.DefaultInputValidator
 */
public interface InputValidator {
    CompletableFuture<String> validate(String rule, String input);
}
