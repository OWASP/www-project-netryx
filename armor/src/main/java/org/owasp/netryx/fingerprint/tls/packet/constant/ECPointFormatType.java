package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * ECPointFormat
 * Represents a TLS EC point format
 * <p>
 * UNCOMPRESSED - uncompressed point
 * ANSIX962_COMPRESSED_PRIME - compressed point in prime field
 * ANSIX962_COMPRESSED_CHAR2 - compressed point in characteristic-2 field
 */
public enum ECPointFormatType implements TlsPacket {
    UNCOMPRESSED(0x00),
    ANSIX962_COMPRESSED_PRIME(0x01),
    ANSIX962_COMPRESSED_CHAR2(0x02),
    UNKNOWN(-1);

    private final int id;

    ECPointFormatType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt8(id);
    }

    public static ECPointFormatType of(int id) {
        return Arrays.stream(values()).filter(s -> s.id == id)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
