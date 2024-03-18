package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.ECPointFormatType;
import org.owasp.netryx.util.UInts;

public class ECPointFormat implements TlsPacket {
    private final int id;
    private final ECPointFormatType type;

    public ECPointFormat(int id, ECPointFormatType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public ECPointFormatType getType() {
        return type;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt8(id);
    }
}
