package org.owasp.netryx.constant;

public enum HtmlEncoding {
    ALL("&<>'\"/=`"),
    CONTENT("&<>"),
    ATTRIBUTE("&<>'\"/=`"),
    SINGLE_QUOTED_ATTRIBUTE("&<'/=`"),
    DOUBLE_QUOTED_ATTRIBUTE("&<\"/=`");

    private final long validMask;

    HtmlEncoding(String charsToEncode) {
        long encodeMask = 0;

        for (int i = 0; i < charsToEncode.length(); i++)
            encodeMask |= 1L << charsToEncode.charAt(i);

        this.validMask = encodeMask;
    }

    public long getValidMask() {
        return validMask;
    }
}
