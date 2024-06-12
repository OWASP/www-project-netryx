package org.owasp.netryx.validator;

public interface ValidatorProvider {
    PathValidator path();

    InputValidator input();
}
