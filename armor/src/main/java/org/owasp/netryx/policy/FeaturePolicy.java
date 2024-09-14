package org.owasp.netryx.policy;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * FeaturePolicy
 * Security policy that sets the Feature-Policy header
 */
public class FeaturePolicy implements SecurityPolicy {
    // defines the default policy for features
    private Map<String, String> directives = new HashMap<>();

    public void setDirective(String name, String value) {
        directives.put(name, value);
    }

    public void setDirectives(Map<String, String> directives) {
        this.directives = directives;
    }

    private String buildHeader() {
        var policyBuilder = new StringJoiner("; ", "", ";");

        directives.forEach((feature, value) ->
                policyBuilder.add(feature + " " + value)
        );

        return policyBuilder.toString();
    }

    @Override
    public void apply(ResponseHeaders responseHeaders) {
        if (directives.isEmpty())
            return;

        responseHeaders.set(HEADER_NAME, buildHeader());
    }

    private static final String HEADER_NAME = "Feature-Policy";
}
