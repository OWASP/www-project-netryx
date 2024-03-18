package org.owasp.netryx.encoder;

/**
 * HtmlEncoder
 * HTML encoder interface.
 * <p>
 * HTML encoding is one of the most important parts of the application.
 * Improper HTML encoding can lead to XSS vulnerabilities.
 *
 * @see DefaultHtmlEncoder
 */
public interface HtmlEncoder {
    // Encode input string to be used in HTML.
    String encode(String input);

    //
    String sanitize(String html);
}
