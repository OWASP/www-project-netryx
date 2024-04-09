package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * ProtocolVersion
 * Represents a TLS protocol version
 * <p>
 * TLS_1_0 - TLS 1.0
 * TLS_1_1 - TLS 1.1
 * TLS_1_2 - TLS 1.2
 * TLS_1_3 - TLS 1.3
 */
public enum ProtocolVersion implements TlsPacket, Greaseable {
    TLS_1_0(0x0301, "10"),
    TLS_1_1(0x0302, "11"),
    TLS_1_2(0x0303, "12"),
    TLS_1_3(0x0304, "13"),
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
    UNKNOWN(-1, "00");

    private final int id;
    private final String name;

    ProtocolVersion(int id, String name) {
        this.id = id;
        this.name = name;
    }

    ProtocolVersion(int id) {
        this(id, "");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isGrease() {
        return name().startsWith("GREASE");
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }

    public static ProtocolVersion of(int uint16) {
        return Arrays.stream(values()).filter(ver -> ver.id == uint16)
                .findFirst().orElse(UNKNOWN);
    }

}
