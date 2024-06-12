package org.owasp.netryx.encoder.impl;

import org.owasp.netryx.encoder.InputEncoder;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DefaultCommandEncoder implements InputEncoder {
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();

    static {
        ENCODE_MAP.put('&', "\\&");
        ENCODE_MAP.put('|', "\\|");
        ENCODE_MAP.put(';', "\\;");
        ENCODE_MAP.put('$', "\\$");
        ENCODE_MAP.put('>', "\\>");
        ENCODE_MAP.put('<', "\\<");
        ENCODE_MAP.put('\\', "\\\\");
        ENCODE_MAP.put('\"', "\\\"");
        ENCODE_MAP.put('\'', "\\'");
        ENCODE_MAP.put('*', "\\*");
    }

    /**
     * Encodes a string for safe use in command-line execution.
     *
     * @param input the string to be encoded
     * @return the encoded string
     */
    @Override
    public String encode(String input) {
        var encoded = new StringBuilder(requireNonNull(input).length() * 2);

        for (var c : input.toCharArray()) {
            if (ENCODE_MAP.containsKey(c)) {
                encoded.append(ENCODE_MAP.get(c));
            } else {
                encoded.append(c);
            }
        }

        return encoded.toString();
    }
}