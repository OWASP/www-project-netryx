package org.owasp.netryx.policy;

import java.util.StringJoiner;

/**
 * XssProtection
 * Security policy that sets the X-XSS-Protection header
 */
public class XssProtection implements SecurityPolicy {
    // enables the Cross-site scripting (XSS) filter built into most recent web browsers
    private boolean enabled = true;
    // defines the mode of the XSS filter
    private String mode = "block";

    // defines the report URI of the XSS filter
    private String report = "";

    public boolean isEnabled() {
        return enabled;
    }

    public String getMode() {
        return mode;
    }

    public String getReport() {
        return report;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public void apply(ResponseHeaders responseHeaders) {
        var joiner = new StringJoiner("; ");

        if (!enabled) {
            joiner.add("0");
            responseHeaders.set(HEADER_NAME, joiner.toString().trim());

            return;
        }

        joiner.add("1");

        if (!mode.isEmpty())
            joiner.add(String.format("mode=%s", mode));

        if (!report.isEmpty())
            joiner.add(report);

        responseHeaders.set(HEADER_NAME, joiner.toString().trim());
    }

    private static final String HEADER_NAME = "X-XSS-Protection";
}
