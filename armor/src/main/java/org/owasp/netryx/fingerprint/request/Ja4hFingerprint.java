package org.owasp.netryx.fingerprint.request;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.netryx.constant.HttpProtocol;
import org.owasp.netryx.util.Hash;

import java.util.Locale;
import java.util.stream.Collectors;

public class Ja4hFingerprint {
    private final HttpProtocol protocol;
    private final HttpRequest request;

    public Ja4hFingerprint(HttpProtocol protocol, HttpRequest request) {
        this.protocol = protocol;
        this.request = request;
    }

    public String getValue() {
        return "%s_%s_%s_%s".formatted(generateJa4ha(), generateJa4hb(), generateJa4hc(), generateJa4hd());
    }

    private String generateJa4ha() {
        var httpMethod = request.method().name().substring(0, 2).toLowerCase();
        var httpVersion = protocol.getNumber();

        var cookiePresence = request.headers().contains("Cookie") ? "c" : "n";
        var refererPresence = request.headers().contains("Referer") ? "r" : "n";

        var headerCount = request.headers().names().stream()
                .map(String::toLowerCase)
                .filter(name -> !name.equalsIgnoreCase("Cookie") && !name.equalsIgnoreCase("Referer"))
                .toArray().length;

        var acceptLanguage = extractLanguage(request.headers().get("Accept-Language"));
        return String.format("%s%s%s%s%d%s", httpMethod, httpVersion, cookiePresence, refererPresence, headerCount, acceptLanguage);
    }

    private String generateJa4hb() {
        var headerNames = request.headers().names().stream()
                .sorted()
                .reduce((header1, header2) -> header1 + "," + header2)
                .orElse("");

        var headersHash = Hex.toHexString(Hash.sha256(headerNames.getBytes()));

        return headersHash.substring(0, 12);
    }

    private String generateJa4hc() {
        var cookieString = extractCookieString(request);

        var cookies = ServerCookieDecoder.STRICT.decode(cookieString);

        var sortedCookieNames = cookies.stream()
                .map(Cookie::name)
                .sorted()
                .collect(Collectors.joining(","));

        return Hex.toHexString(Hash.sha256(sortedCookieNames.getBytes())).substring(0, 12);
    }

    private String generateJa4hd() {
        var cookieString = extractCookieString(request);

        var cookies = ServerCookieDecoder.STRICT.decode(cookieString);

        var sortedCookieNameValues = cookies.stream()
                .map(cookie -> cookie.name() + "=" + cookie.value())
                .sorted()
                .collect(Collectors.joining(","));

        return Hex.toHexString(Hash.sha256(sortedCookieNameValues.getBytes()))
                .substring(0, 12);
    }

    private static String extractLanguage(String header) {
        if (header == null)
            return "0000";

        try {
            var ranges = Locale.LanguageRange.parse(header);

            return ranges.stream()
                    .filter(range -> range.getWeight() == 1.0)
                    .findFirst()
                    .map(range -> range.getRange()
                            .replace("_", "")
                            .replace("-", "")
                            .toLowerCase()
                    )
                    .map(language -> String.format("%-4s", language).replace(' ', '0')
                            .substring(0, 4))
                    .orElse("0000");
        } catch (IllegalArgumentException e) {
            return "0000";
        }
    }

    private static String extractCookieString(HttpRequest request) {
        var cookieString = request.headers().get("Cookie");
        return cookieString == null ? "" : cookieString;
    }
}
