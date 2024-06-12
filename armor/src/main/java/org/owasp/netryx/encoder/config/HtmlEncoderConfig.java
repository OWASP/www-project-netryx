package org.owasp.netryx.encoder.config;

import org.owasp.netryx.constant.HtmlEncoding;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

import java.io.IOException;

public class HtmlEncoderConfig {
    private HtmlEncoding mode = HtmlEncoding.ALL;
    private Policy policy = initializeDefaultPolicy();

    public HtmlEncoding getMode() {
        return mode;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setMode(HtmlEncoding mode) {
        this.mode = mode;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    private static Policy initializeDefaultPolicy() {
        try (var slashdot = HtmlEncoderConfig.class.getClassLoader().getResourceAsStream("antisamy-slashdot.xml")) {
            if (slashdot == null)
                throw new IllegalStateException("No antisamy-slashdot in classpath");

            return Policy.getInstance(slashdot);
        } catch (IOException | PolicyException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HtmlEncoderConfig withMode(HtmlEncoding mode) {
        return new HtmlEncoderConfig() {{
            setMode(mode);
        }};
    }

    public static HtmlEncoderConfig withPolicy(Policy policy) {
        return new HtmlEncoderConfig() {{
            setPolicy(policy);
        }};
    }

    public static HtmlEncoderConfig create(HtmlEncoding mode, Policy policy) {
        return new HtmlEncoderConfig() {{
            setMode(mode);
            setPolicy(policy);
        }};
    }
}
