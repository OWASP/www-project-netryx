package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.CipherType;
import org.owasp.netryx.util.UInts;

public class CipherSuite implements TlsPacket {
    private final int id;
    private final CipherType type;

    public CipherSuite(int id, CipherType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public CipherType getType() {
        return type;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }
}
