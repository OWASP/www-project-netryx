package org.owasp.netryx.encoder.config;

import org.owasp.netryx.constant.JavaScriptEncoding;

public class JavaScriptEncoderConfig {
    private JavaScriptEncoding mode = JavaScriptEncoding.HTML;
    private boolean asciiOnly = false;

    public JavaScriptEncoding getMode() {
        return mode;
    }

    public boolean isAsciiOnly() {
        return asciiOnly;
    }

    public void setMode(JavaScriptEncoding mode) {
        this.mode = mode;
    }

    public void setAsciiOnly(boolean asciiOnly) {
        this.asciiOnly = asciiOnly;
    }

    public static JavaScriptEncoderConfig create(JavaScriptEncoding mode, boolean asciiOnly) {
        return new JavaScriptEncoderConfig() {{
            setMode(mode);
            setAsciiOnly(asciiOnly);
        }};
    }

    public static JavaScriptEncoderConfig withMode(JavaScriptEncoding mode) {
        return create(mode, false);
    }
}
