package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.CompressionType;
import org.owasp.netryx.util.UInts;

public class CompressionMethod implements TlsPacket {
    private final int id;
    private final CompressionType type;

    public CompressionMethod(int id, CompressionType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public CompressionType getType() {
        return type;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }
}

