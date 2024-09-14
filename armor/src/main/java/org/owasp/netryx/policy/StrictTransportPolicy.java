package org.owasp.netryx.policy;

import java.util.StringJoiner;

/**
 * StrictTransportPolicy
 * Security policy that sets the Strict-Transport-Security header
 */
public class StrictTransportPolicy implements SecurityPolicy {
    // defines the maximum amount of time the browser should remember that a site is only to be accessed using HTTPS
    private long maxAge = 3600L;

    // indicates whether the policy applies to subdomains
    private boolean includeSubDomains = false;
    // indicates whether the policy should be included in the HTTP response
    private boolean preload = false;

    public boolean isIncludeSubDomains() {
        return includeSubDomains;
    }

    public boolean isPreload() {
        return preload;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public void setIncludeSubDomains(boolean includeSubDomains) {
        this.includeSubDomains = includeSubDomains;
    }

    public void setPreload(boolean preload) {
        this.preload = preload;
    }

    @Override
    public void apply(ResponseHeaders responseHeaders) {
        if (maxAge == 0L)
            return;

        var joiner = new StringJoiner("; ");
        joiner.add(String.format("max-age=%s", maxAge));

        if (!includeSubDomains) {
            responseHeaders.set(HEADER_NAME, joiner.toString().trim());
            return;
        }

        joiner.add("includeSubDomains");

        if (preload)
            joiner.add("preload");

        responseHeaders.set(HEADER_NAME, joiner.toString().trim());
    }

    private static final String HEADER_NAME = "Strict-Transport-Security";
}
