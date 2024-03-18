package org.owasp.netryx.validator;

import org.owasp.netryx.validator.impl.DefaultPathValidator;

/**
 * FileValidator
 * File validator interface.
 * <p>
 * Will save you from path traversal attacks.
 * Allowed paths can be defined in the configuration files
 *
 * @see DefaultPathValidator
 */
public interface PathValidator {
    String validate(String path);
}
