package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * SignAlgorithm
 * Represents a TLS signature algorithm
 */
public enum SignAlgorithm implements TlsPacket, Greaseable {
    RSA_PKCS1_SHA256(0x0401),
    ECDSA_SECP256r1_SHA256(0x0403),
    RSA_PKCS1_SHA384(0x0501),
    ECDSA_SECP384r1_SHA384(0x0503),
    RSA_PKCS1_SHA512(0x0601),
    ECDSA_SECP521r1_SHA512(0x0603),
    RSA_PKCS1_SHA1(0x0201),
    ECDSA_SHA1(0x0203),
    ECDSA_SECP256R1_SHA256(0x0403),
    ECDSA_SECP384R1_SHA384(0x0503),
    ECDSA_SECP521R1_SHA512(0x0603),
    ED25519(0x0807),
    ED448(0x0808),
    RSA_PSS_PSS_SHA256(0x0809),
    RSA_PSS_PSS_SHA384(0x080A),
    RSA_PSS_PSS_SHA512(0x080B),
    RSA_PSS_RSAE_SHA256(0x0804),
    RSA_PSS_RSAE_SHA384(0x0805),
    RSA_PSS_RSAE_SHA512(0x0806),
    ECDSA_SHA224(0x0303),
    RSA_PKCS1_SHA224(0x0301),
    DSA_SHA224(0x0302),
    DSA_SHA1(0x0202),
    DSA_SHA256(0x0402),
    DSA_SHA384(0x0502),
    DSA_SHA512(0x0602),
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

    SignAlgorithm(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean isGrease() {
        return name().startsWith("GREASE");
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }

    public static SignAlgorithm of(int id) {
        return Arrays.stream(values()).filter(s -> s.id == id)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
