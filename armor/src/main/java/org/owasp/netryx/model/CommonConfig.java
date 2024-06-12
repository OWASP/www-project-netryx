package org.owasp.netryx.model;

import org.owasp.netryx.model.settings.Http1Settings;
import org.owasp.netryx.policy.*;
import org.owasp.netryx.util.PatternUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonConfig {
    private boolean limitRequests = true;
    private boolean limitRapidReset = true;
    private boolean enableSecurityPolicy = true;

    private Map<String, String> patterns = PatternUtil.loadDefaults();
    private String baseDirectory = null;

    private ValidatorConfig validator = new ValidatorConfig();

    private LimiterConfig requestLimiter = new LimiterConfig();
    private LimiterConfig resetStreamLimiter = new LimiterConfig();

    private Http1Settings http1 = new Http1Settings();

    private List<SecurityPolicy> policies = new ArrayList<>(List.of(
            new ContentSecurityPolicy(), new ContentTypeOptions(),
            new CorsPolicy(), new FeaturePolicy(),
            new FrameOptions(), new ReferrerPolicy(),
            new StrictTransportPolicy(), new XssProtection()
    ));

    public void setPatterns(Map<String, String> patterns) {
        this.patterns = patterns;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void setValidator(ValidatorConfig validator) {
        this.validator = validator;
    }

    public void setRequestLimiter(LimiterConfig requestLimiter) {
        this.requestLimiter = requestLimiter;
    }

    public void setResetStreamLimiter(LimiterConfig resetStreamLimiter) {
        this.resetStreamLimiter = resetStreamLimiter;
    }

    public void setPolicies(List<SecurityPolicy> policies) {
        this.policies = policies;
    }

    public void setEnableSecurityPolicy(boolean enableSecurityPolicy) {
        this.enableSecurityPolicy = enableSecurityPolicy;
    }

    public void setLimitRapidReset(boolean limitRapidReset) {
        this.limitRapidReset = limitRapidReset;
    }

    public void setLimitRequests(boolean limitRequests) {
        this.limitRequests = limitRequests;
    }

    public void setHttp1(Http1Settings http1) {
        this.http1 = http1;
    }

    public Map<String, String> getPatterns() {
        return patterns;
    }

    public LimiterConfig getRequestLimiter() {
        return requestLimiter;
    }

    public LimiterConfig getResetStreamLimiter() {
        return resetStreamLimiter;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public ValidatorConfig getValidator() {
        return validator;
    }

    public List<SecurityPolicy> getPolicies() {
        return policies;
    }

    public boolean isEnableSecurityPolicy() {
        return enableSecurityPolicy;
    }

    public boolean isLimitRequests() {
        return limitRequests;
    }

    public boolean isLimitRapidReset() {
        return limitRapidReset;
    }

    public Http1Settings getHttp1() {
        return http1;
    }
}
