package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.exception.TlsException;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * TlsRandom
 * Represents a TLS random packet used in the ClientHello and ServerHello packets
 */
public final class TlsRandom implements TlsPacket {
    private byte[] bytes;

    public TlsRandom(UIntInputStream in) {
        try {
            var random = new byte[32];
            var bytesRead = in.read(random, 0, 32);

            if (bytesRead != 32) {
                throw new IllegalArgumentException("Bad random");
            }

            this.bytes = random;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public TlsRandom(byte[] bytes) {
        this.bytes = bytes;
    }

    public TlsRandom() {
        try (var out = new UIntByteArrayOutputStream()) {
            var now = Instant.now().getEpochSecond();

            var randomBytes = new byte[28];
            new SecureRandom().nextBytes(randomBytes);

            out.writeUInt32(now);
            out.writeBytes(randomBytes);

            this.bytes = out.toByteArray();
        } catch (IOException e) {
            throw new TlsException(e);
        }
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }
}
