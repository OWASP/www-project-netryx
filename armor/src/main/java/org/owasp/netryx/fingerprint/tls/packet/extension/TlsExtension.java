package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.fingerprint.tls.packet.constant.Greaseable;
import org.owasp.netryx.util.io.UIntInputStream;

/**
 * Extension
 * Represents a TLS extension
 */
public abstract class TlsExtension implements TlsPacket {
    protected final int id;
    protected final ExtensionType type;

    public TlsExtension(int id, ExtensionType type) {
        this.id = id;
        this.type = type;
    }

    public abstract void read(UIntInputStream in);

    public int id() {
        return id;
    }

    public ExtensionType getType() {
        return type;
    }
}
