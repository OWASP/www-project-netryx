package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.SignAlgorithm;
import org.owasp.netryx.util.UInts;

public class SignatureAlgorithm implements TlsPacket {
    private final int id;
    private final SignAlgorithm type;

    public SignatureAlgorithm(int id, SignAlgorithm type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public SignAlgorithm getType() {
        return type;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt16(id);
    }
}

