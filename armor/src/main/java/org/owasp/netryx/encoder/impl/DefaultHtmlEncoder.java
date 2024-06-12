package org.owasp.netryx.encoder.impl;

import org.owasp.netryx.encoder.config.HtmlEncoderConfig;
import org.owasp.netryx.exception.SanitizeException;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.owasp.netryx.util.Chars.isInvalidXmlChar;
import static org.owasp.netryx.util.Chars.isNonCharacter;

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
    private static final Map<Character, String> ESCAPE_CHARS = new HashMap<>();

    static {
        ESCAPE_CHARS.put('<', "&lt;");
        ESCAPE_CHARS.put('>', "&gt;");
        ESCAPE_CHARS.put('&', "&amp;");
        ESCAPE_CHARS.put('\"', "&quot;");
        ESCAPE_CHARS.put('\'', "&#39;");
        ESCAPE_CHARS.put('=', "&#x3D;");
        ESCAPE_CHARS.put('`', "&#x60;");
    }

    private final long validMask;
    private final AntiSamy antiSamy;

    public DefaultHtmlEncoder(HtmlEncoderConfig config) {
        this.validMask = config.getMode().getValidMask();
        this.antiSamy = new AntiSamy(requireNonNull(config.getPolicy()));
    }

    /**
     * Used for encoding input to be displayed in HTML.
     * Encoded example: "&lt;script&gt;alert('xss')&lt;/script&gt;"
     */
    @Override
    public String encode(String input) {
        var encoded = new StringBuilder(requireNonNull(input).length() * 2);

        for (var i = 0; i < input.length(); i++) {
            var ch = input.charAt(i);

            if (Character.isHighSurrogate(ch)) {
                i = encodeSurrogatePair(input, encoded, i);
            } else if (Character.isLowSurrogate(ch)) {
                encoded.append("&#xfffd;");
            } else {
                encodeCharacter(encoded, ch);
            }
        }

        return encoded.toString();
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

    /**
     * Encodes a surrogate pair or a lone high surrogate.
     *
     * @param input The input string
     * @param encoded The StringBuilder to append the encoded character to
     * @param i The current index
     * @return The updated index
     */
    private int encodeSurrogatePair(String input, StringBuilder encoded, int i) {
        var high = input.charAt(i);

        if (i + 1 < input.length() && Character.isLowSurrogate(input.charAt(i + 1))) {
            var codePoint = Character.toCodePoint(high, input.charAt(i + 1));
            if (isNonCharacter(codePoint) || isInvalidXmlChar(codePoint)) {
                encoded.append("&#xfffd;");
            } else {
                encoded.append("&#").append(codePoint).append(";");
            }
            i++;
        } else {
            encoded.append("&#xfffd;");
        }

        return i;
    }

    /**
     * Encodes a single character.
     *
     * @param encoded The StringBuilder to append the encoded character to.
     * @param ch The character to be encoded.
     */
    private void encodeCharacter(StringBuilder encoded, char ch) {
        if ((ch < 32 && ch != '\t' && ch != '\n' && ch != '\r') || (ch >= 127 && ch <= 159) || isInvalidXmlChar(ch)) {
            encoded.append("&#xfffd;");
        } else {
            var replacement = ESCAPE_CHARS.get(ch);

            if (replacement != null && (validMask & (1L << ch)) != 0) {
                encoded.append(replacement);
            } else {
                encoded.append(ch);
            }
        }
    }
}
