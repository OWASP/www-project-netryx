package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * CompressionMethod
 * Represents a TLS compression method
 * <p>
 * NULL - no compression
 * DEFLATE - DEFLATE compression
 * LZS - LZS compression
 */
public enum CompressionType implements TlsPacket {
    NULL(0x00),
    DEFLATE(0x01),
    LZS(0x40),
    UNKNOWN(-1);

    private final int id;

    CompressionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }

    public static CompressionType of(int uint16) {
        return Arrays.stream(values()).filter(c -> c.id == uint16).findFirst().orElse(UNKNOWN);
    }
}
