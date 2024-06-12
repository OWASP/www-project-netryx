package org.owasp.netryx.encoder.impl;

import org.owasp.netryx.constant.JavaScriptEncoding;
import org.owasp.netryx.encoder.InputEncoder;
import org.owasp.netryx.encoder.config.JavaScriptEncoderConfig;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.owasp.netryx.util.Chars.*;
import static org.owasp.netryx.util.Chars.toHex;

/**
 * DefaultJavaScriptEncoder
 * This class provides a mechanism for safely encoding strings to be used in JavaScript contexts,
 * such as attributes, blocks, and HTML content, to prevent XSS (Cross-Site Scripting) attacks.
 * It supports encoding of special characters and surrogate pairs, ensuring that the output is safe for inclusion
 * in JavaScript code, attributes, and HTML.
 */
public class DefaultJavaScriptEncoder implements InputEncoder {
    private static final Map<Character, String> ESCAPE_SEQUENCES = new HashMap<>();

    static {
        ESCAPE_SEQUENCES.put('\b', "\\b");
        ESCAPE_SEQUENCES.put('\t', "\\t");
        ESCAPE_SEQUENCES.put('\n', "\\n");
        ESCAPE_SEQUENCES.put('\f', "\\f");
        ESCAPE_SEQUENCES.put('\r', "\\r");
        ESCAPE_SEQUENCES.put('\'', "\\x27");
        ESCAPE_SEQUENCES.put('\"', "\\x22");
        ESCAPE_SEQUENCES.put('\\', "\\\\");
        ESCAPE_SEQUENCES.put('<', "\\x3C");
        ESCAPE_SEQUENCES.put('>', "\\x3E");
        ESCAPE_SEQUENCES.put('&', "\\x26");
        ESCAPE_SEQUENCES.put('-', "\\x2D");
        ESCAPE_SEQUENCES.put('/', "\\x2F");
    }

    private final boolean hexEncodeQuotes;
    private final int[] validMasks;
    private final boolean asciiOnly;

    public DefaultJavaScriptEncoder(JavaScriptEncoding mode, boolean asciiOnly) {
        this.hexEncodeQuotes = (mode == JavaScriptEncoding.ATTRIBUTE || mode == JavaScriptEncoding.HTML);
        this.asciiOnly = asciiOnly;
        this.validMasks = initializeValidMasks(mode, asciiOnly);
    }

    public DefaultJavaScriptEncoder(JavaScriptEncoderConfig config) {
        this(config.getMode(), config.isAsciiOnly());
    }

    /**
     * Initializes the valid character masks based on the encoding mode and ASCII-only flag.
     *
     * @param mode the JavaScript encoding mode.
     * @param asciiOnly whether to only allow ASCII characters.
     * @return an array of integer masks for valid characters.
     */
    private int[] initializeValidMasks(JavaScriptEncoding mode, boolean asciiOnly) {
        var masks = new int[]{
                0,
                -1 & ~((1 << '\'') | (1 << '\"')),
                -1 & ~((1 << '\\')),
                asciiOnly ? ~(1 << 127) : -1
        };

        if (mode == JavaScriptEncoding.BLOCK || mode == JavaScriptEncoding.HTML) {
            masks[1] &= ~((1 << '/') | (1 << '-'));
        }
        masks[1] &= ~(1 << '&');

        if (mode == JavaScriptEncoding.ATTRIBUTE || mode == JavaScriptEncoding.HTML) {
            masks[1] &= ~(1 << '<');
            masks[1] &= ~(1 << '>');
        }
        return masks;
    }

    /**
     * Encodes the input string for safe inclusion in JavaScript code, attributes, or HTML.
     *
     * @param input the input string to encode.
     * @return the encoded string.
     */
    @Override
    public String encode(String input) {
        var encoded = new StringBuilder(requireNonNull(input).length() * 2);

        for (var i = 0; i < input.length(); i++) {
            var ch = input.charAt(i);
            if (Character.isHighSurrogate(ch)) {
                i = encodeSurrogatePair(input, encoded, i);
            } else if (Character.isLowSurrogate(ch)) {
                encoded.append("\\uFFFD");
            } else {
                encodeCharacter(encoded, ch);
            }
        }

        return encoded.toString();
    }

    /**
     * Encodes a surrogate pair or a lone high surrogate.
     *
     * @param input the input string.
     * @param encoded the StringBuilder to append the encoded character to.
     * @param i the current index.
     * @return the updated index.
     */
    private int encodeSurrogatePair(String input, StringBuilder encoded, int i) {
        var high = input.charAt(i);
        if (i + 1 < input.length() && Character.isLowSurrogate(input.charAt(i + 1))) {
            var codePoint = Character.toCodePoint(high, input.charAt(i + 1));

            if (isNonCharacter(codePoint) || isInvalidXmlChar(codePoint)) {
                encoded.append("\\uFFFD");
            } else {
                var low = input.charAt(i + 1);
                encoded.append("\\u").append(toHex(high, 4)).append("\\u").append(toHex(low, 4));
            }
            i++;
        } else {
            encoded.append("\\uFFFD");
        }
        return i;
    }

    /**
     * Encodes a single character.
     *
     * @param encoded the StringBuilder to append the encoded character to.
     * @param ch the character to encode.
     */
    private void encodeCharacter(StringBuilder encoded, char ch) {
        if (ch < 128) {
            if ((validMasks[ch >>> 5] & (1 << ch)) == 0) {
                encodeSpecialCharacter(encoded, ch);
            } else {
                encoded.append(ch);
            }
        } else if (asciiOnly || ch == '\u2028' || ch == '\u2029') {
            encoded.append("\\u").append(toHex(ch, 4));
        } else {
            encoded.append(ch);
        }
    }

    /**
     * Encodes special characters using predefined escape sequences or hex encoding.
     *
     * @param encoded the StringBuilder to append the encoded character to.
     * @param ch the character to encode.
     */
    private void encodeSpecialCharacter(StringBuilder encoded, char ch) {
        var escape = ESCAPE_SEQUENCES.get(ch);

        if (escape != null) {
            encoded.append(escape);
        } else if (hexEncodeQuotes && (ch == '\'' || ch == '\"')) {
            encoded.append("\\x").append(toHex(ch, 2));
        } else {
            encoded.append("\\u").append(toHex(ch, 4));
        }
    }
}
