package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * NamedGroup
 * Represents a TLS named group
 */
public enum NamedGroup implements TlsPacket {
    SECT163K1(0x0001),
    SECT163R1(0x0002),
    SECT163R2(0x0003),
    SECT193R1(0x0004),
    SECT193R2(0x0005),
    SECT233K1(0x0006),
    SECT233R1(0x0007),
    SECT239K1(0x0008),
    SECT283K1(0x0009),
    SECT283R1(0x000a),
    SECT409K1(0x000b),
    SECT409R1(0x000c),
    SECT571K1(0x000d),
    SECT571R1(0x000e),
    SECP160K1(0x000f),
    SECP160R1(0x0010),
    SECP160R2(0x0011),
    SECP192K1(0x0012),
    SECP192R1(0x0013),
    SECP224K1(0x0014),
    SECP224R1(0x0015),
    SECP256K1(0x0016),
    SECP256R1(0x0017),
    SECP384R1(0x0018),
    SECP521R1(0x0019),
    BRAINPOOLP256R1(0x001a),
    BRAINPOOLP384R1(0x001b),
    BRAINPOOLP512R1(0x001c),
    X25519(0x001d),
    X448(0x001e),
    BRAINPOOLP256R1TLS13(0x001f),
    BRAINPOOLP384R1TLS13(0x0020),
    BRAINPOOLP512R1TLS13(0x0021),
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

    NamedGroup(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isGrease() {
        return name().startsWith("GREASE");
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }

    public static NamedGroup of(int id) {
        return Arrays.stream(values()).filter(s -> s.id == id)
                .findFirst()
                .orElse(UNKNOWN);
    }
}