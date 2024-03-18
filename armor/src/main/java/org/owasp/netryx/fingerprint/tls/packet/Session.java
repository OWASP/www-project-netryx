package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Session
 * Represents a TLS session packet
 */
public final class Session implements TlsPacket {
    private byte[] session;

    public Session(UIntInputStream in) {
        try {
            var sessionLength = in.readUInt8();

            var session = new byte[sessionLength];
            var bytesRead = in.read(session, 0, sessionLength);

            if (bytesRead != sessionLength) {
                throw new IllegalArgumentException("Bad input provided");
            }

            this.session = session;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Session(byte[] session) {
        this.session = session;
    }

    public Session() {
        this(new byte[0]);
    }

    public void setSession(byte[] session) {
        this.session = session;
    }

    public byte[] getSession() {
        return session;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt8(session.length);
            out.writeBytes(session);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
