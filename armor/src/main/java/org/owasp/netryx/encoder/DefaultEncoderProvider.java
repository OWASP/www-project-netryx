package org.owasp.netryx.encoder;

import org.owasp.netryx.encoder.config.HtmlEncoderConfig;
import org.owasp.netryx.encoder.config.JavaScriptEncoderConfig;
import org.owasp.netryx.encoder.impl.*;

public class DefaultEncoderProvider implements EncoderProvider {
    private static final InputEncoder DEFAULT_LDAP = new DefaultLDAPEncoder();
    private static final InputEncoder DEFAULT_CMD = new DefaultCommandEncoder();

    @Override
    public HtmlEncoder html(HtmlEncoderConfig config) {
        return new DefaultHtmlEncoder(config);
    }

    @Override
    public InputEncoder js(JavaScriptEncoderConfig config) {
        return new DefaultJavaScriptEncoder(config);
    }

    @Override
    public InputEncoder ldap() {
        return DEFAULT_LDAP;
    }

    @Override
    public InputEncoder cmd() {
        return DEFAULT_CMD;
    }
}
