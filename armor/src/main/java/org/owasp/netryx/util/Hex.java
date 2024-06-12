package org.owasp.netryx.util;

public final class Hex {
    private Hex() {}

    public static String toHexString(byte[] bytes) {
        var hexString = new StringBuilder(2 * bytes.length);
        for (var b : bytes) {
            var hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] decode(String hex) {
        var length = hex.length();
        var bytes = new byte[length / 2];

        for (var i = 0; i < length; i += 2)
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));

        return bytes;
    }
}
