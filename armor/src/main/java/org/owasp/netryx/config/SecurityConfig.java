package org.owasp.netryx.config;

import org.owasp.netryx.encoder.HtmlEncoder;
import org.owasp.netryx.model.LimiterConfig;
import org.owasp.netryx.model.ValidatorConfig;
import org.owasp.netryx.password.config.ArgonConfig;
import org.owasp.netryx.password.config.BCryptConfig;
import org.owasp.netryx.password.config.SCryptConfig;
import org.owasp.netryx.password.constant.EncoderType;
import org.owasp.netryx.policy.SecurityPolicy;
import org.owasp.netryx.model.settings.Http1Settings;
import org.owasp.netryx.validator.PathValidator;

import java.nio.file.Path;
import java.util.List;

public interface SecurityConfig {
    EncoderType encoderType();

    String pattern(String rule);

    ValidatorConfig validatorConfig();

    Path baseDirectory();

    LimiterConfig rapidResetConfig();

    LimiterConfig requestLimiterConfig();

    HtmlEncoder htmlEncoder();

    PathValidator fileValidator();

    List<SecurityPolicy> policies();

    BCryptConfig bcryptConfig();

    SCryptConfig scryptConfig();

    ArgonConfig argonConfig();

    boolean limitRequests();

    boolean limitRapidReset();

    boolean enableSecurityPolicy();

    Http1Settings http1Settings();
}
