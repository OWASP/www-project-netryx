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
public enum ProtocolVersion implements TlsPacket {
    TLS_1_0(0x0301),
    TLS_1_1(0x0302),
    TLS_1_2(0x0303),
    TLS_1_3(0x0304),
    UNKNOWN(-1);

    private final int id;

    ProtocolVersion(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
