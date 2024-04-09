package org.owasp.netryx.fingerprint.tls;

import org.apache.hc.client5.http.utils.Hex;
import org.owasp.netryx.fingerprint.tls.packet.constant.ProtocolVersion;
import org.owasp.netryx.fingerprint.tls.packet.extension.TlsExtension;
import org.owasp.netryx.fingerprint.tls.packet.model.CipherSuite;
import org.owasp.netryx.fingerprint.tls.packet.model.ECPointFormat;
import org.owasp.netryx.fingerprint.tls.packet.model.EllipticCurve;
import org.owasp.netryx.util.Hash;

import java.util.ArrayList;
import java.util.Arrays;
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
public class Ja3Fingerprint implements TlsFingerprint {
    private int tlsVersion = ProtocolVersion.TLS_1_3.getId();

    private List<Integer> cipherSuites = new ArrayList<>();
    private List<Integer> extensions = new ArrayList<>();
    private List<Integer> namedGroups = new ArrayList<>();
    private List<Integer> pointFormats = new ArrayList<>();

    public String getRaw() {
        var joiner = new StringJoiner(",");
        joiner.add(String.valueOf(tlsVersion));

        joiner.add(join(cipherSuites, String::valueOf));
        joiner.add(join(extensions, String::valueOf));
        joiner.add(join(namedGroups, String::valueOf));
        joiner.add(join(pointFormats, String::valueOf));

        return joiner.toString();
    }

    public String getHash() {
        return Hex.encodeHexString(Hash.md5(getRaw().getBytes()));
    }

    public int getTlsVersion() {
        return tlsVersion;
    }

    @Override
    public List<Integer> getCipherSuites() {
        return cipherSuites;
    }

    @Override
    public List<Integer> getExtensions() {
        return extensions;
    }

    @Override
    public List<Integer> getNamedGroups() {
        return namedGroups;
    }

    @Override
    public List<Integer> getPointFormats() {
        return pointFormats;
    }

    public void setCipherSuites(List<Integer> cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    public void setExtensions(List<Integer> extensions) {
        this.extensions = extensions;
    }

    public void setNamedGroups(List<Integer> namedGroups) {
        this.namedGroups = namedGroups;
    }

    public void setPointFormats(List<Integer> pointFormats) {
        this.pointFormats = pointFormats;
    }

    public void setTlsVersion(int tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    @Override
    public String toString() {
        return getRaw();
    }

    public static TlsFingerprintBuilder newBuilder() {
        return new TlsFingerprintBuilder();
    }

    public static Ja3Fingerprint parse(String fingerprint) {
        var components = fingerprint.split(",");

        if (components.length < 5)
            throw new IllegalArgumentException("Not JA3 String");

        var tlsVersion = Integer.parseInt(components[0]);

        var cipherSuites = listOf(components[1]);
        var extensions = listOf(components[2]);
        var namedGroups = listOf(components[3]);
        var pointFormats = listOf(components[4]);

        return new Ja3Fingerprint() {{
            setTlsVersion(tlsVersion);
            setCipherSuites(cipherSuites);
            setExtensions(extensions);
            setNamedGroups(namedGroups);
            setPointFormats(pointFormats);
        }};
    }

    private static List<Integer> listOf(String component) {
        return Arrays.stream(component.split("-"))
                .map(Integer::valueOf)
                .toList();
    }

    private static <T> String join(List<T> list, Function<T, String> stringFunction) {
        var joiner = new StringJoiner("-");

        for (var element : list)
            joiner.add(stringFunction.apply(element));

        return joiner.toString();
    }

    public static class TlsFingerprintBuilder {
        private int tlsVersion = ProtocolVersion.TLS_1_3.getId();

        private List<CipherSuite> cipherSuites = new ArrayList<>();
        private List<TlsExtension> extensions = new ArrayList<>();

        private List<EllipticCurve> namedGroups = new ArrayList<>();
        private List<ECPointFormat> pointFormats = new ArrayList<>();

        public TlsFingerprintBuilder setVersion(int tlsVersion) {
            this.tlsVersion = tlsVersion;
            return this;
        }

        public TlsFingerprintBuilder setCipherSuites(List<CipherSuite> cipherSuites) {
            this.cipherSuites = cipherSuites.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());

            return this;
        }

        public TlsFingerprintBuilder setExtensions(List<TlsExtension> extensions) {
            this.extensions = extensions.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());
            return this;
        }

        public TlsFingerprintBuilder setNamedGroups(List<EllipticCurve> namedGroups) {
            this.namedGroups = namedGroups.stream()
                    .filter(packet -> !packet.getType().isGrease())
                    .collect(Collectors.toList());
            return this;
        }

        public TlsFingerprintBuilder setPointFormats(List<ECPointFormat> pointFormats) {
            this.pointFormats = pointFormats;
            return this;
        }

        public Ja3Fingerprint build() {
            return new Ja3Fingerprint() {{
                setTlsVersion(tlsVersion);
                setCipherSuites(cipherSuites.stream().map(CipherSuite::getId).toList());
                setExtensions(extensions.stream().map(TlsExtension::id).toList());
                setNamedGroups(namedGroups.stream().map(EllipticCurve::getId).toList());
                setPointFormats(pointFormats.stream().map(ECPointFormat::getId).toList());
            }};
        }
    }
}
