package org.owasp.netryx.encoder;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.netryx.exception.SanitizeException;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import static java.util.Objects.requireNonNull;

/**
 * DefaultHtmlEncoder
 * Default HTML encoder implementation.
 * <p>
 * HTML encoding is one of the most important parts of the application.
 * Improper HTML encoding can lead to XSS vulnerabilities.
 *
 * @see HtmlEncoder
 */
public class DefaultHtmlEncoder implements HtmlEncoder {
    private final AntiSamy antiSamy;

    public DefaultHtmlEncoder(Policy policy) {
        this.antiSamy = new AntiSamy(policy);
    }

    public DefaultHtmlEncoder() {
        this.antiSamy = new AntiSamy();
    }

    /**
     * Used for encoding input to be displayed in HTML.
     * For instance, following input: "<script>alert('xss')</script>"
     * will be encoded to: "&lt;script&gt;alert('xss')&lt;/script&gt;"
     */
    @Override
    public String encode(String input) {
        return StringEscapeUtils.escapeHtml4(requireNonNull(input));
    }

    /**
     * Sanitizes HTML returned for the user.
     */
    @Override
    public String sanitize(String html) {
        try {
            return antiSamy.scan(requireNonNull(html)).getCleanHTML();
        } catch (ScanException | PolicyException e) {
            throw new SanitizeException(e.getMessage());
        }
    }
}
