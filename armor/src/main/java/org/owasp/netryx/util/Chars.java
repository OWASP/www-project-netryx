package org.owasp.netryx.util;

public final class Chars {
    private Chars() {}

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public static boolean isNonCharacter(int codePoint) {
        return (codePoint & 0xFFFE) == 0xFFFE || (codePoint >= 0xFDD0 && codePoint <= 0xFDEF);
    }

    public static boolean isInvalidXmlChar(int ch) {
        return (ch <= 0x1F && ch != '\t' && ch != '\n' && ch != '\r') ||
                (ch >= 0x7F && ch <= 0x9F) ||
                (ch >= 0xFDD0 && ch <= 0xFDEF) ||
                (ch & 0xFFFE) == 0xFFFE ||
                (ch > 0x10FFFF);
    }

    public static String toHex(int codePoint, int length) {
        StringBuilder hex = new StringBuilder(length);
        for (int i = (length - 1) * 4; i >= 0; i -= 4) {
            hex.append(HEX[(codePoint >>> i) & 0xF]);
        }
        return hex.toString();
    }
}
