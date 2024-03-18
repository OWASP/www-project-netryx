package org.owasp.netryx.config;

import org.owasp.netryx.encoder.DefaultHtmlEncoder;
import org.owasp.netryx.encoder.HtmlEncoder;
import org.owasp.netryx.exception.NotConfiguredFieldException;
import org.owasp.netryx.exception.UnknownRuleException;
import org.owasp.netryx.model.CommonConfig;
import org.owasp.netryx.model.LimiterConfig;
import org.owasp.netryx.model.ValidatorConfig;
import org.owasp.netryx.password.config.ArgonConfig;
import org.owasp.netryx.password.config.BCryptConfig;
import org.owasp.netryx.password.config.SCryptConfig;
import org.owasp.netryx.password.constant.EncoderType;
import org.owasp.netryx.policy.SecurityPolicy;
import org.owasp.netryx.model.settings.Http1Settings;
import org.owasp.netryx.validator.PathValidator;
import org.owasp.netryx.validator.impl.DefaultPathValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * CommonSecurityConfig
 * Common security configuration implementation.
 * <p>
 * This class is used to provide common security configuration
 * for all armor modules by JSON configuration file.
 *
 * @see CommonConfig
 */
public class CommonSecurityConfig implements SecurityConfig {
    private final CommonConfig config;

    public CommonSecurityConfig(CommonConfig config) {
        this.config = config;
    }

    @Override
    public EncoderType encoderType() {
        return config.getEncoderType();
    }

    @Override
    public String pattern(String rule) {
        return Optional.ofNullable(config.getPatterns().get(rule))
                .orElseThrow(() -> new UnknownRuleException(rule));
    }

    @Override
    public ValidatorConfig validatorConfig() {
        return config.getValidator();
    }

    @Override
    public Path baseDirectory() {
        var directory = config.getBaseDirectory();

        return Optional.ofNullable(directory)
                .map(dir -> Paths.get(dir).toAbsolutePath().normalize())
                .orElseThrow(() -> new NotConfiguredFieldException("baseDirectory"));
    }

    @Override
    public LimiterConfig rapidResetConfig() {
        return config.getResetStreamLimiter();
    }

    @Override
    public LimiterConfig requestLimiterConfig() {
        return config.getRequestLimiter();
    }

    @Override
    public HtmlEncoder htmlEncoder() {
        return new DefaultHtmlEncoder();
    }

    @Override
    public PathValidator fileValidator() {
        return new DefaultPathValidator(this);
    }

    @Override
    public List<SecurityPolicy> policies() {
        return config.getPolicies();
    }

    @Override
    public BCryptConfig bcryptConfig() {
        return Optional.ofNullable(config.getBcrypt())
                .orElseThrow(() -> new NotConfiguredFieldException("bcrypt"));
    }

    @Override
    public SCryptConfig scryptConfig() {
        return Optional.ofNullable(config.getScrypt())
                .orElseThrow(() -> new NotConfiguredFieldException("scrypt"));
    }

    @Override
    public ArgonConfig argonConfig() {
        return Optional.ofNullable(config.getArgon())
                .orElseThrow(() -> new NotConfiguredFieldException("argon"));
    }

    @Override
    public boolean limitRequests() {
        return config.isLimitRequests();
    }

    @Override
    public boolean limitRapidReset() {
        return config.isLimitRapidReset();
    }

    @Override
    public boolean enableSecurityPolicy() {
        return config.isEnableSecurityPolicy();
    }

    @Override
    public Http1Settings http1Settings() {
        return config.getHttp1();
    }
}
