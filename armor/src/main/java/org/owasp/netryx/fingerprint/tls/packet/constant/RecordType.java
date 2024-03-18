package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * RecordType
 * Represents a TLS record type
 * <p>
 * CHANGE_CIPHER_SPEC - change cipher spec
 * ALERT_RECORD - alert record
 * HANDSHAKE_RECORD - handshake record
 * APPLICATION_RECORD - application record
 */
public enum RecordType implements TlsPacket {
    CHANGE_CIPHER_SPEC(0x14),
    ALERT_RECORD(0x15),
    HANDSHAKE_RECORD(0x16),
    APPLICATION_RECORD(0x17);

    private final int id;

    RecordType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt8(id);
    }

    public static RecordType of(int id) {
        return Arrays.stream(values()).filter(s -> s.id == id)
                .findFirst()
                .orElseThrow();
    }
}
