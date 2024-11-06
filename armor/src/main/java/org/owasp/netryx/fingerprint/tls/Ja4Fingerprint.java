package org.owasp.netryx.fingerprint.tls;

import org.owasp.netryx.constant.TransportProtocol;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;
import org.owasp.netryx.fingerprint.tls.packet.constant.*;
import org.owasp.netryx.fingerprint.tls.packet.extension.*;
import org.owasp.netryx.fingerprint.tls.packet.model.CipherSuite;
import org.owasp.netryx.fingerprint.tls.packet.model.ECPointFormat;
import org.owasp.netryx.fingerprint.tls.packet.model.EllipticCurve;
import org.owasp.netryx.fingerprint.tls.packet.model.SignatureAlgorithm;
import org.owasp.netryx.util.Hash;
import org.owasp.netryx.util.Hex;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.APPLICATION_LAYER_PROTOCOL_NEGOTIATION;

public class Ja4Fingerprint implements TlsFingerprint {
    private TransportProtocol transportProtocol = TransportProtocol.TCP;
    private final ClientHello hello;

    public Ja4Fingerprint(ClientHello hello) {
        this.hello = hello;
    }

    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(TransportProtocol transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    @Override
    public List<Integer> getCipherSuites() {
        var ciphers = hello.getCipherSuites().getCiphers()
                .stream()
                .map(CipherSuite::getType)
                .collect(Collectors.toList());

        return filterGrease(ciphers, CipherType::getId);
    }

    @Override
    public List<Integer> getNamedGroups() {
        var extension = hello.getExtensions().getExtension(ExtensionType.SUPPORTED_GROUPS, SupportedGroups.class);

        if (extension == null)
            extension = new SupportedGroups();

        var curves = extension.getCurves().stream()
                .map(EllipticCurve::getType)
                .collect(Collectors.toList());

        return filterGrease(curves, NamedGroup::getId);
    }

    @Override
    public List<Integer> getExtensions() {
        var extension = hello.getExtensions();

        var extensions = extension.getExtensions()
                .stream()
                .map(TlsExtension::getType)
                .collect(Collectors.toList());

        return filterGrease(extensions, ExtensionType::getId);
    }

    @Override
    public List<Integer> getPointFormats() {
        var extension = hello.getExtensions().getExtension(ExtensionType.EC_POINT_FORMATS, PointFormats.class);

        if (extension == null)
            extension = new PointFormats();

        var formats = extension.getPointFormats().stream()
                .map(ECPointFormat::getType)
                .collect(Collectors.toList());

        return filterGrease(formats, ECPointFormatType::getId);
    }

    @Override
    public String getRaw() {
        return String.format("%s_%s_%s", generateJa4a(), generateHexJoinedString(getCipherSuites()), generateJa4cRaw());
    }

    @Override
    public String getHash() {
        return String.format("%s_%s_%s", generateJa4a(), hashFirst12Characters(generateHexJoinedString(getCipherSuites())),
                hashFirst12Characters(generateJa4cRaw()));
    }

    private String generateJa4a() {
        var tlsVersion = getTlsVersion();
        var sni = hello.getExtensions().getExtension(ExtensionType.SERVER_NAME, ServerName.class);
        // Cap both cipher and extension count at 99 as per the specification:
        // https://github.com/FoxIO-LLC/ja4/blob/53ad7eaf1abce2a653eebb1b4a4196f08be7f94d/technical_details/JA4.md?plain=1#L55
        var ciphersCount = Math.min(99, getCipherSuites().size());
        var extensionsCount = Math.min(99, getExtensions().size());
        var alpnProtocol = getFirstAlpnProtocol();

        return String.format("%s%s%s%02d%02d%s",
                transportProtocol.name().toLowerCase().charAt(0),
                tlsVersion,
                (sni == null ? "i" : "d"),
                ciphersCount,
                extensionsCount,
                (alpnProtocol.isEmpty() ? "00" : alpnProtocol));
    }

    private String getFirstAlpnProtocol() {
        var alpn = hello.getExtensions().getExtension(APPLICATION_LAYER_PROTOCOL_NEGOTIATION, AlpnExtension.class);
        return alpn == null || alpn.getProtocols().isEmpty() ? "" : alpn.getProtocols().get(0);
    }

    private String getTlsVersion() {
        var versions = hello.getExtensions().getExtension(ExtensionType.SUPPORTED_VERSIONS, SupportedVersions.class);
        return (versions == null) ? hello.getVersion().getProtocolVersion().getName() : findMaxNonGreaseVersion(versions.getVersions()).getName();
    }

    private String generateJa4cRaw() {
        var hexExtensions = generateHexJoinedString(filterExtensions(getExtensions()));
        var hexSignatures = generateHexJoinedString(getSignatureAlgorithms());
        return String.format("%s_%s", hexExtensions, hexSignatures);
    }

    private List<Integer> filterExtensions(List<Integer> extensions) {
        return extensions.stream()
                .filter(id -> id != APPLICATION_LAYER_PROTOCOL_NEGOTIATION.getId() && id != ExtensionType.SERVER_NAME.getId())
                .collect(Collectors.toList());
    }

    private List<Integer> getSignatureAlgorithms() {
        var signatureAlgorithms = hello.getExtensions().getExtension(ExtensionType.SIGNATURE_ALGORITHMS, SignatureAlgorithms.class);
        if (signatureAlgorithms == null) signatureAlgorithms = new SignatureAlgorithms();

        var algorithms = signatureAlgorithms.getAlgorithms()
                .stream()
                .map(SignatureAlgorithm::getType)
                .collect(Collectors.toList());

        return filterGrease(algorithms, SignAlgorithm::getId);
    }

    private static  <T> List<Integer> filterGrease(List<T> list, Function<T, Integer> mapper) {
        return list.stream()
                .filter(item -> !((Greaseable) item).isGrease())
                .map(mapper)
                .collect(Collectors.toList());
    }

    private static String generateHexJoinedString(List<Integer> list) {
        return list.stream().sorted().map(i -> String.format("%04x", i)).collect(Collectors.joining(","));
    }

    private static String hashFirst12Characters(String input) {
        return Hex.toHexString(Hash.sha256(input.getBytes())).substring(0, 12);
    }

    private static ProtocolVersion findMaxNonGreaseVersion(List<ProtocolVersion> versions) {
        return versions.stream()
                .filter(version -> !version.isGrease())
                .max(Comparator.comparingInt(ProtocolVersion::getId))
                .orElse(ProtocolVersion.UNKNOWN);
    }
}
