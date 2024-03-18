package org.owasp.netryx.policy;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

import java.util.HashSet;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static org.apache.hc.core5.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;

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
            Set.of(GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name())
    );

    // defines valid headers
    private Set<String> allowedHeaders = new HashSet<>(
            Set.of(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderNames.AUTHORIZATION.toString())
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

    @Override
    public void apply(HttpResponse response) {
        if (!enabled)
            return;

        var origins = String.join(", ", allowedOrigins);
        var methods = String.join(", ", allowedMethods);
        var headers = String.join(", ", allowedHeaders);

        var responseHeaders = response.headers();
        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origins);
        responseHeaders.set(ACCESS_CONTROL_ALLOW_METHODS, methods);
        responseHeaders.set(ACCESS_CONTROL_ALLOW_HEADERS, headers);
        responseHeaders.set(ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(allowCredentials));
        responseHeaders.set(ACCESS_CONTROL_MAX_AGE, String.valueOf(maxAge));
    }
}
