package org.owasp.netryx.fingerprint.tls.ja3;

import org.apache.hc.client5.http.utils.Hex;
import org.owasp.netryx.fingerprint.tls.packet.constant.ProtocolVersion;
import org.owasp.netryx.fingerprint.tls.packet.extension.TlsExtension;
import org.owasp.netryx.fingerprint.tls.packet.model.CipherSuite;
import org.owasp.netryx.fingerprint.tls.packet.model.ECPointFormat;
import org.owasp.netryx.fingerprint.tls.packet.model.EllipticCurve;
import org.owasp.netryx.util.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Ja3Fingerprint
 * Represents a JA3 fingerprint and its MD5 hash.
 * <p>
 * JA3 is a method for creating SSL/TLS client fingerprints that are easy to produce
 * and can be easily shared for threat intelligence.
 */
public class Ja3Fingerprint {
    private final String value;
    private final String md5;

    public Ja3Fingerprint(String value, String md5) {
        this.value = value;
        this.md5 = md5;
    }

    public String getValue() {
        return value;
    }

    public String getMd5() {
        return md5;
    }

    public static Ja3Builder newBuilder() {
        return new Ja3Builder();
    }

    public static class Ja3Builder {
        private int tlsVersion = ProtocolVersion.TLS_1_3.getId();

        private List<CipherSuite> cipherSuites = new ArrayList<>();
        private List<TlsExtension> extensions = new ArrayList<>();

        private List<EllipticCurve> namedGroups = new ArrayList<>();
        private List<ECPointFormat> pointFormats = new ArrayList<>();

        public Ja3Builder setVersion(int tlsVersion) {
            this.tlsVersion = tlsVersion;
            return this;
        }

        public Ja3Builder setCipherSuites(List<CipherSuite> cipherSuites) {
            this.cipherSuites = cipherSuites.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());

            return this;
        }

        public Ja3Builder setExtensions(List<TlsExtension> extensions) {
            this.extensions = extensions.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());
            return this;
        }

        public Ja3Builder setNamedGroups(List<EllipticCurve> namedGroups) {
            this.namedGroups = namedGroups.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());
            return this;
        }

        public Ja3Builder setPointFormats(List<ECPointFormat> pointFormats) {
            this.pointFormats = pointFormats;
            return this;
        }

        public Ja3Fingerprint build() {
            var joiner = new StringJoiner(",");
            joiner.add(String.valueOf(tlsVersion));

            joiner.add(join(cipherSuites, cipher -> String.valueOf(cipher.getId())));
            joiner.add(join(extensions, ext -> String.valueOf(ext.id())));
            joiner.add(join(namedGroups, ng -> String.valueOf(ng.getId())));
            joiner.add(join(pointFormats, pf -> String.valueOf(pf.getId())));

            var ja3 = joiner.toString();
            var md5 = Hex.encodeHexString(Hash.md5(ja3.getBytes()));

            return new Ja3Fingerprint(ja3, md5);
        }

        private static <T> String join(List<T> list, Function<T, String> stringFunction) {
            var joiner = new StringJoiner("-");

            for (var element : list)
                joiner.add(stringFunction.apply(element));

            return joiner.toString();
        }
    }
}
