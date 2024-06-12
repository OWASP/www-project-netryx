package org.owasp.netryx.validator;

import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.validator.impl.DefaultPathValidator;
import org.owasp.netryx.validator.impl.DefaultInputValidator;

public class DefaultValidatorProvider implements ValidatorProvider {
    private final PathValidator pathValidator;
    private final InputValidator inputValidator;

    public DefaultValidatorProvider(SecurityConfig config) {
        this.pathValidator = new DefaultPathValidator(config);
        this.inputValidator = new DefaultInputValidator(config);
    }

    @Override
    public PathValidator path() {
        return pathValidator;
    }

    @Override
    public InputValidator input() {
        return inputValidator;
    }
}
