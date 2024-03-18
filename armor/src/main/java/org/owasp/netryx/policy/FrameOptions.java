package org.owasp.netryx.policy;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

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
    public void apply(HttpResponse response) {
        if (directive == Directive.NONE)
            return;

        response.headers().set(HttpHeaderNames.X_FRAME_OPTIONS, directive.name());
    }

    public enum Directive {
        NONE, // disables framing
        SAMEORIGIN, // allows framing from the same origin
        DENY // prevents framing from all origins
    }
}
