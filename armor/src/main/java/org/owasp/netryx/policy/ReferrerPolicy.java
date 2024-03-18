package org.owasp.netryx.policy;

import io.netty.handler.codec.http.HttpResponse;

/**
 * ReferrerPolicy
 * Security policy that sets the Referrer-Policy header
 */
public class ReferrerPolicy implements SecurityPolicy {
    // controls how much referrer information (sent via the Referer header) should be included with requests
    private Directive directive = Directive.NONE;

    public Directive getDirective() {
        return directive;
    }

    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    @Override
    public void apply(HttpResponse response) {
        var headerValue = directive.name;

        if (headerValue == null)
            return;

        response.headers().set(HEADER_NAME, headerValue);
    }

    public enum Directive {
        // disables the referrer information
        NONE(null),
        // excludes the referrer information when navigating from HTTPS to HTTP
        NO_REFERRER("no-referrer"),
        // excludes the referrer information when navigating from HTTPS to HTTP
        NO_REFERRER_WHEN_DOWNGRADE("no-referrer-when-downgrade"),
        // excludes the path and query string
        ORIGIN("origin"),
        // excludes the path and query string when navigating from HTTPS to HTTP
        ORIGIN_WHEN_CROSS_ORIGIN("origin-when-cross-origin"),
        // excludes the path and query string when navigating to a different origin
        SAME_ORIGIN("same-origin"),
        // excludes the path and query string when navigating to a different origin
        // and excludes the referrer information when navigating from HTTPS to HTTP
        STRICT_ORIGIN("strict-origin"),
        // excludes the path and query string when navigating to a different origin and excludes
        // the referrer information when navigating from HTTPS to HTTP
        STRICT_ORIGIN_WHEN_CROSS_ORIGIN("strict-origin-when-cross-origin");

        private final String name;

        Directive(String name) {
            this.name = name;
        }
    }

    private static final String HEADER_NAME = "Referrer-Policy";
}
