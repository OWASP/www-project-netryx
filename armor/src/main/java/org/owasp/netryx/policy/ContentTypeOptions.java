package org.owasp.netryx.policy;

/**
 * ContentTypeOptions
 * Security policy that sets the X-Content-Type-Options header
 */
public class ContentTypeOptions implements SecurityPolicy {
    // prevents the browser from MIME-sniffing a response away from the declared content-type
    private boolean noSniff = true;

    public void setNoSniff(boolean noSniff) {
        this.noSniff = noSniff;
    }

    public boolean isNoSniff() {
        return noSniff;
    }

    @Override
    public void apply(ResponseHeaders responseHeaders) {
        if (noSniff)
            responseHeaders.set(HEADER_NAME, "nosniff");
    }

    private static final String HEADER_NAME = "X-Content-Type-Options";
}
