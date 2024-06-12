package org.owasp.netryx.encoder.impl;

import org.owasp.netryx.encoder.InputEncoder;
import org.owasp.netryx.encoder.impl.DefaultHtmlEncoder;

/**
 * HtmlEncoder
 * HTML encoder interface.
 * <p>
 * HTML encoding is one of the most important parts of the application.
 * Improper HTML encoding can lead to XSS vulnerabilities.
 *
 * @see DefaultHtmlEncoder
 */
public interface HtmlEncoder extends InputEncoder {
    String sanitize(String html);
}
