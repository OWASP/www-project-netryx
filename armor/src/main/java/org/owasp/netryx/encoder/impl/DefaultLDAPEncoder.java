package org.owasp.netryx.encoder.impl;

import org.owasp.netryx.encoder.InputEncoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DefaultLDAPEncoder implements InputEncoder  {
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();

    static {
        ENCODE_MAP.put('\\', "\\5c");
        ENCODE_MAP.put('*', "\\2a");
        ENCODE_MAP.put('(', "\\28");
        ENCODE_MAP.put(')', "\\29");
        ENCODE_MAP.put('\0', "\\00");
        ENCODE_MAP.put('/', "\\2f");
        ENCODE_MAP.put('#', "\\23");
        ENCODE_MAP.put('+', "\\2b");
        ENCODE_MAP.put('<', "\\3c");
        ENCODE_MAP.put('>', "\\3e");
        ENCODE_MAP.put(',', "\\2c");
        ENCODE_MAP.put(';', "\\3b");
        ENCODE_MAP.put('"', "\\22");
        ENCODE_MAP.put('=', "\\3d");
    }

    /**
     * Encodes a string for safe use in LDAP distinguished names and search filters.
     *
     * @param input the string to be encoded
     * @return the encoded string
     */
    @Override
    public String encode(String input) {
        var encoded = new StringBuilder(requireNonNull(input).length() * 2);

        for (var c : input.toCharArray()) {
            encoded.append(encodeCharacter(c));
        }

        return encoded.toString();
    }

    private static String encodeCharacter(char c) {
        if (ENCODE_MAP.containsKey(c)) {
            return ENCODE_MAP.get(c);
        } else if (c < 0x20 || c >= 0x7f) {
            var bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
            var encoded = new StringBuilder();

            for (var b : bytes)
                encoded.append(String.format("\\%02x", b));

            return encoded.toString();
        } else {
            return String.valueOf(c);
        }
    }
}