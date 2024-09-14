package org.owasp.netryx.policy;

/**
 * FrameOptions
 * Security policy that sets the X-Frame-Options header
 */
public class FrameOptions implements SecurityPolicy {
    // prevents the browser from rendering a page in a frame
    private Directive directive = Directive.SAMEORIGIN;

    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    public Directive getDirective() {
        return directive;
    }

    @Override
    public void apply(ResponseHeaders responseHeaders) {
        if (directive == Directive.NONE)
            return;

        responseHeaders.set("X-Frame-Options", directive.name());
    }

    public enum Directive {
        NONE, // disables framing
        SAMEORIGIN, // allows framing from the same origin
        DENY // prevents framing from all origins
    }
}
