package org.owasp.netryx.fingerprint.request;

import org.owasp.netryx.util.Hash;
import org.owasp.netryx.util.Hex;

import java.net.HttpCookie;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Ja4hFingerprint {
    private final String httpMethod;
    private final String httpVersion;
    private final boolean hasCookie;
    private final boolean hasReferer;
    private final int headerCount;
    private final String acceptLanguage;
    private final Map<String, String> headers;
    private final String cookieString;

    private Ja4hFingerprint(Builder builder) {
        this.httpMethod = builder.httpMethod;
        this.httpVersion = builder.httpVersion;
        this.headers = builder.headers;

        this.cookieString = builder.getHeader("Cookie");
        this.headerCount = builder.headers.size();
        this.acceptLanguage = extractLanguage(builder.getHeader("Accept-Language"));

        this.hasCookie = builder.hasHeader("Cookie");
        this.hasReferer = builder.hasHeader("Referer");
    }

    public String getValue() {
        return String.format("%s_%s_%s_%s", generateJa4ha(), generateJa4hb(), generateJa4hc(), generateJa4hd());
    }

    private String generateJa4ha() {
        var methodPrefix = httpMethod.substring(0, 2).toLowerCase();
        var cookiePresence = hasCookie ? "c" : "n";
        var refererPresence = hasReferer ? "r" : "n";

        return String.format("%s%s%s%s%d%s", methodPrefix, httpVersion, cookiePresence, refererPresence, headerCount, acceptLanguage);
    }

    private String generateJa4hb() {
        var headerNames = headers.keySet().stream()
                .sorted()
                .collect(Collectors.joining(","));

        var headersHash = Hex.toHexString(Hash.sha256(headerNames.getBytes()));

        return headersHash.substring(0, 12);
    }

    private String generateJa4hc() {
        var cookies = HttpCookie.parse(cookieString);

        var sortedCookieNames = cookies.stream()
                .map(HttpCookie::getName)
                .sorted()
                .collect(Collectors.joining(","));

        return Hex.toHexString(Hash.sha256(sortedCookieNames.getBytes())).substring(0, 12);
    }

    private String generateJa4hd() {
        var cookies = HttpCookie.parse(cookieString);

        var sortedCookieNameValues = cookies.stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .sorted()
                .collect(Collectors.joining(","));

        return Hex.toHexString(Hash.sha256(sortedCookieNameValues.getBytes()))
                .substring(0, 12);
    }

    private static String extractLanguage(String header) {
        if (header == null) return "0000";

        try {
            var ranges = Locale.LanguageRange.parse(header);
            return ranges.stream()
                    .filter(range -> range.getWeight() == 1.0)
                    .findFirst()
                    .map(range -> range.getRange()
                            .replace("_", "")
                            .replace("-", "")
                            .toLowerCase())
                    .map(language -> String.format("%-4s", language).replace(' ', '0').substring(0, 4))
                    .orElse("0000");
        } catch (IllegalArgumentException e) {
            return "0000";
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String httpMethod;
        private String httpVersion;
        private Map<String, String> headers = new LinkedHashMap<>();

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.put(name.toLowerCase(), value);
            return this;
        }

        public String getHeader(String name) {
            return headers.get(name.toLowerCase());
        }

        public boolean hasHeader(String name) {
            return headers.containsKey(name.toLowerCase());
        }

        public Ja4hFingerprint build() {
            return new Ja4hFingerprint(this);
        }
    }
}
