package org.owasp.netryx.config;

import org.owasp.netryx.model.LimiterConfig;
import org.owasp.netryx.model.ValidatorConfig;
import org.owasp.netryx.model.settings.Http1Settings;
import org.owasp.netryx.policy.SecurityPolicy;
import org.owasp.netryx.validator.PathValidator;

import java.nio.file.Path;
import java.util.List;

public interface SecurityConfig {
    String pattern(String rule);

    ValidatorConfig validatorConfig();

    Path baseDirectory();

    LimiterConfig rapidResetConfig();

    LimiterConfig requestLimiterConfig();

    PathValidator fileValidator();

    List<SecurityPolicy> policies();

    boolean limitRequests();

    boolean limitRapidReset();

    boolean enableSecurityPolicy();

    Http1Settings http1Settings();
}
