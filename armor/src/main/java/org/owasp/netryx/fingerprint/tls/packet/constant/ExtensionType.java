package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.extension.*;
import org.owasp.netryx.fingerprint.tls.packet.model.Extension;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * ExtensionType
 * <p>
 * Represents a TLS extension types.
 */
public enum ExtensionType implements TlsPacket {
    SERVER_NAME(0x0000) {
        @Override
        public ServerName getExtension() {
            return new ServerName();
        }
    },
    MAX_FRAGMENT_LENGTH(0x0001),
    CLIENT_CERTIFICATE_URL(0x0002),
    TRUSTED_CA_KEYS(0x0003),
    TRUNCATED_HMAC(0x0004),
    STATUS_REQUEST(0x0005),
    USER_MAPPING(0x0006),
    CLIENT_AUTHZ(0x0007),
    SERVER_AUTHZ(0x0008),
    CERT_TYPE(0x0009),
    SUPPORTED_GROUPS(0x000A) {
        @Override
        public SupportedGroups getExtension() {
            return new SupportedGroups();
        }
    },
    EC_POINT_FORMATS(0x000B) {
        @Override
        public PointFormats getExtension() {
            return new PointFormats();
        }
    },
    SRP(0x000C),
    SIGNATURE_ALGORITHMS(0x000D) {
        @Override
        public SignatureAlgorithms getExtension() {
            return new SignatureAlgorithms();
        }
    },
    USE_SRTP(0x000E),
    HEARTBEAT(0x000F),
    APPLICATION_LAYER_PROTOCOL_NEGOTIATION(0x0010),
    STATUS_REQUEST_V2(0x0011),
    SIGNED_CERTIFICATE_TIMESTAMP(0x0012),
    CLIENT_CERTIFICATE_TYPE(0x00013),
    SERVER_CERTIFICATE_TYPE(0x00014),
    PADDING(0x0015),
    ENCRYPT_THEN_MAC(0x0016),
    EXTENDED_MASTER_SECRET(0x0017),
    TOKEN_BINDING(0x0018),
    CACHED_INFO(0x0019),
    TLS_LTS(0x001A),
    COMPRESS_CERTIFICATE(0x001B),
    RECORD_SIZE_LIMIT(0x001C),
    PWD_PROTECT(0x001D),
    PWD_CLEAR(0x001E),
    PASSWORD_SALT(0x001F),
    TICKET_PINNING(0x0020),
    TLS_CERT_WITH_EXTERN_PSK(0x0021),
    DELEGATED_CREDENTIALS(0x0022),
    SESSION_TICKET(0x0023),
    TLMSP(0x0024),
    TLMSP_PROXYING(0x0025),
    TLMSP_DELEGATE(0x0026),
    SUPPORTED_EKT_CIPHERS(0x0027),
    PRE_SHARED_KEY(0x0029),
    EARLY_DATA(0x002A),
    SUPPORTED_VERSIONS(0x002B),
    COOKIE(0x002C),
    PSK_KEY_EXCHANGE_MODES(0x002D),
    CERTIFICATE_AUTHORITIES(0x002F),
    OID_FILTERS(0x0030),
    POST_HANDSHAKE_AUTH(0x0031),
    SIGNATURE_ALGORITHMS_CERT(0x0032),
    KEY_SHARE(0x0033),
    TRANSPARENCY_INFO(0x0034),
    CONNECTION_ID_OLD(0x0035),
    CONNECTION_ID(0x0036),
    EXTERNAL_ID_HASH(0x0037),
    EXTERNAL_SESSION_ID(0x0038),
    QUIC_TRANSPORT_PARAMETERS(0x0039),
    TICKET_REQUEST(0x003A),
    DNSSEC_CHAIN(0x003B),
    RENEGOTIATION_INFO(0xFF01),
    GREASE_00(0x0A0A),
    GREASE_01(0x1A1A),
    GREASE_02(0x2A2A),
    GREASE_03(0x3A3A),
    GREASE_04(0x4A4A),
    GREASE_05(0x5A5A),
    GREASE_06(0x6A6A),
    GREASE_07(0x7A7A),
    GREASE_08(0x8A8A),
    GREASE_09(0x9A9A),
    GREASE_10(0xAAAA),
    GREASE_11(0xBABA),
    GREASE_12(0xCACA),
    GREASE_13(0xDADA),
    GREASE_14(0xEAEA),
    GREASE_15(0xFAFA),
    UNKNOWN(-1);

    private final int id;

    ExtensionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public TlsExtension getExtension() {
        return new Extension(id, this);
    }

    public boolean isGrease() {
        return name().startsWith("GREASE");
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }

    public static ExtensionType of(int id) {
        return Arrays.stream(values()).filter(e -> e.id == id)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
