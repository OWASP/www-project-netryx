package org.owasp.netryx.fingerprint.tls.packet.client;

import org.owasp.netryx.fingerprint.tls.ja3.Ja3Fingerprint;
import org.owasp.netryx.fingerprint.tls.packet.Record;
import org.owasp.netryx.fingerprint.tls.packet.*;
import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.fingerprint.tls.packet.constant.HandshakeType;
import org.owasp.netryx.fingerprint.tls.packet.extension.PointFormats;
import org.owasp.netryx.fingerprint.tls.packet.extension.SupportedGroups;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntDataInputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

/**
 * ClientHello
 * Represents a TLS ClientHello packet
 * <p>
 * The ClientHello packet is the first packet sent by the client to the server,
 * that can be used for fingerprinting the client and bot detections.
 *
 * @see Ja3Fingerprint
 */
public final class ClientHello implements RecordPacket {
    private final Record recordHeader;
    private final Handshake handshakeHeader;
    private final TlsVersion version;
    private final TlsRandom random;
    private final Session session;
    private final CipherSuites cipherSuites;
    private final CompressionMethods compressionMethods;
    private final Extensions extensions;

    public ClientHello(UIntInputStream in) {
        this.recordHeader = new Record(in);
        this.handshakeHeader = new Handshake(in);
        this.version = new TlsVersion(in);
        this.random = new TlsRandom(in);
        this.session = new Session(in);
        this.cipherSuites = new CipherSuites(in);
        this.compressionMethods = new CompressionMethods(in);
        this.extensions = new Extensions(in);
    }

    public ClientHello(
            Record recordHeader, Handshake handshakeHeader,
            TlsVersion version, TlsRandom random,
            Session session, CipherSuites cipherSuites,
            CompressionMethods compressionMethods, Extensions extensions
    ) {
        this.recordHeader = recordHeader;
        this.handshakeHeader = handshakeHeader;
        this.version = version;
        this.random = random;
        this.session = session;
        this.cipherSuites = cipherSuites;
        this.compressionMethods = compressionMethods;
        this.extensions = extensions;
    }

    public Record getRecordHeader() {
        return recordHeader;
    }

    public Handshake getHandshakeHeader() {
        return handshakeHeader;
    }

    public TlsVersion getVersion() {
        return version;
    }

    public TlsRandom getRandom() {
        return random;
    }

    public Session getSession() {
        return session;
    }

    public CipherSuites getCipherSuites() {
        return cipherSuites;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public CompressionMethods getCompressionMethods() {
        return compressionMethods;
    }

    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var messageLength = TlsPacket.sizeOf(version, random, session, cipherSuites, compressionMethods, extensions);

            handshakeHeader.setMessageLength(messageLength);
            recordHeader.setNextDataLength(handshakeHeader.length() + messageLength);

            out.writeBytes(recordHeader.toByteArray());
            out.writeBytes(handshakeHeader.toByteArray());
            out.writeBytes(version.toByteArray());
            out.writeBytes(random.toByteArray());
            out.writeBytes(session.toByteArray());
            out.writeBytes(cipherSuites.toByteArray());
            out.writeBytes(compressionMethods.toByteArray());
            out.writeBytes(extensions.toByteArray());

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean isClientHello(byte[] bytes) {
        try (var in = new UIntDataInputStream(bytes)) {
            var recordType = in.readUInt8();

            if (recordType != 0x16) {
                return false;
            }

            var protocolMajorVersion = in.readUInt8();
            in.readUInt8();

            if (protocolMajorVersion != 0x03)
                return false;

            var recordLength = in.readUInt16();

            if (recordLength > bytes.length - 5)
                return false;

            var handshakeType = in.readUInt8();
            return handshakeType == HandshakeType.CLIENT_HELLO.getId();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Record getRecord() {
        return recordHeader;
    }

    /**
     * ja3
     * Generates a JA3 fingerprint from the ClientHello packet
     *
     * @return the JA3 fingerprint
     */
    public Ja3Fingerprint ja3() {
        var namedGroups = extensions.getExtension(ExtensionType.SUPPORTED_GROUPS,
                SupportedGroups.class);
        var pointFormats = extensions.getExtension(ExtensionType.EC_POINT_FORMATS, PointFormats.class);

        return Ja3Fingerprint.newBuilder()
                .setVersion(version.getProtocolVersion().getId())
                .setCipherSuites(cipherSuites.getCiphers())
                .setExtensions(extensions.getExtensions())
                .setNamedGroups(namedGroups == null ? new ArrayList<>() : namedGroups.getCurves())
                .setPointFormats(pointFormats == null ? new ArrayList<>() : pointFormats.getPointFormats())
                .build();
    }
}
