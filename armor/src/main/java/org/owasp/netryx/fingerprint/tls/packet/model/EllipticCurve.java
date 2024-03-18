package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.NamedGroup;
import org.owasp.netryx.util.UInts;

public class EllipticCurve implements TlsPacket {
    private final int id;
    private final NamedGroup type;

    public EllipticCurve(int id, NamedGroup type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public NamedGroup getType() {
        return type;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }
}

