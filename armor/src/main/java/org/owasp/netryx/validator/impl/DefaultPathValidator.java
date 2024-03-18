package org.owasp.netryx.validator.impl;

import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.exception.ValidationException;
import org.owasp.netryx.validator.PathValidator;

import java.nio.file.Paths;

/**
 * DefaultFileValidator
 * Default file validator implementation.
 * <p>
 * Will save you from path traversal attacks.
 * Allowed paths can be defined in the configuration files
 *
 * @see PathValidator
 */
public class DefaultPathValidator implements PathValidator {
    private final SecurityConfig config;

    public DefaultPathValidator(SecurityConfig config) {
        this.config = config;
    }

    @Override
    public String validate(String path) {
        if (path == null || path.trim().isEmpty())
            throw new ValidationException("file", "Path cannot be null or empty");

        var finalPath = Paths.get(path).toAbsolutePath().normalize();

        if (finalPath.startsWith(config.baseDirectory()))
            return finalPath.toString();

        throw new ValidationException("file", "Path is not allowed");
    }
}
