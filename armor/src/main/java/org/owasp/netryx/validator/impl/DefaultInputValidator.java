package org.owasp.netryx.validator.impl;

import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.exception.ValidationException;
import org.owasp.netryx.validator.InputValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * DefaultInputValidator
 * Default input validator implementation based on RegEx.
 * <p>
 * Will save you from input attacks.
 * Rules can be defined in the configuration file.
 * <p>
 * @see org.owasp.netryx.validator.InputValidator
 */
public class DefaultInputValidator implements InputValidator {
    private final Map<String, Pattern> patternCache = new HashMap<>();

    private final SecurityConfig config;
    private final Executor executor;

    public DefaultInputValidator(SecurityConfig config) {
        this.config = config;
        this.executor = Executors.newFixedThreadPool(config.validatorConfig().getPoolSize());
    }

    @Override
    public CompletableFuture<String> validate(String rule, String input) {
        return CompletableFuture.supplyAsync(() -> {
            var valid = validationPattern(rule).matcher(input)
                    .matches();

            if (!valid)
                throw new ValidationException(rule, input);

            return input;
        }, executor).orTimeout(config.validatorConfig().getMaxTime(), TimeUnit.MILLISECONDS);
    }

    private Pattern validationPattern(String name) {
        return patternCache.computeIfAbsent(name, key ->
                Pattern.compile(config.pattern(name))
        );
    }
}
