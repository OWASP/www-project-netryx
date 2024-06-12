package org.owasp.netryx.encoder;

import org.owasp.netryx.constant.JavaScriptEncoding;
import org.owasp.netryx.encoder.config.HtmlEncoderConfig;
import org.owasp.netryx.encoder.config.JavaScriptEncoderConfig;
import org.owasp.netryx.encoder.impl.HtmlEncoder;

public interface EncoderProvider {
    HtmlEncoder html(HtmlEncoderConfig config);

    InputEncoder js(JavaScriptEncoderConfig config);

    InputEncoder ldap();

    InputEncoder cmd();

    default HtmlEncoder html() {
        return html(new HtmlEncoderConfig());
    }

    default InputEncoder js(JavaScriptEncoding mode) {
        return js(JavaScriptEncoderConfig.withMode(mode));
    }
}
