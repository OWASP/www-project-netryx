package org.owasp.netryx.validator;

public interface Validator {
    PathValidator path();

    InputValidator input();
}
