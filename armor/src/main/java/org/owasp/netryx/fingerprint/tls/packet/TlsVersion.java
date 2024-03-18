package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.fingerprint.tls.packet.constant.ProtocolVersion;
import org.owasp.netryx.util.io.UIntInputStream;

/**
 * TlsVersion
 * Represents a TLS version packet
 */
public final class TlsVersion implements TlsPacket {
    private ProtocolVersion protocolVersion;

    public TlsVersion(UIntInputStream in) {
        this.protocolVersion = ProtocolVersion.of(in.readUInt16());
    }

    public TlsVersion(ProtocolVersion version) {
        this.protocolVersion = version;
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public byte[] toByteArray() {
        return protocolVersion.toByteArray();
    }
}
