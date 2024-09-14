package org.owasp.netryx.policy;

import java.util.HashSet;
import java.util.Set;

/**
 * CorsPolicy
 * Security policy that configures Cross-Origin Resource Sharing
 */
public class CorsPolicy implements SecurityPolicy {
    // enables CORS
    private boolean enabled = false;

    // defines valid origins
    private Set<String> allowedOrigins = new HashSet<>(Set.of("*"));
    // defines valid methods
    private Set<String> allowedMethods = new HashSet<>(
            Set.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );

    // defines valid headers
    private Set<String> allowedHeaders = new HashSet<>(
            Set.of("Content-Type", "Authorization")
    );

    // indicates whether the response to the request can be exposed
    private boolean allowCredentials = false;
    // indicates how long the results of a preflight request can be cached
    private int maxAge = 3600;

    public boolean isEnabled() {
        return enabled;
    }

    public Set<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    public Set<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setAllowedOrigins(Set<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public void setAllowedMethods(Set<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void setAllowedHeaders(Set<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // replace constants with actual names
    @Override
    public void apply(ResponseHeaders responseHeaders) {
        if (!enabled)
            return;

        var origins = String.join(", ", allowedOrigins);
        var methods = String.join(", ", allowedMethods);
        var headers = String.join(", ", allowedHeaders);

        responseHeaders.set("Access-Control-Allow-Origin", origins);
        responseHeaders.set("Access-Control-Allow-Methods", methods);
        responseHeaders.set("Access-Control-Allow-Headers", headers);
        responseHeaders.set("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
        responseHeaders.set("Access-Control-Max-Age", String.valueOf(maxAge));
    }
}
