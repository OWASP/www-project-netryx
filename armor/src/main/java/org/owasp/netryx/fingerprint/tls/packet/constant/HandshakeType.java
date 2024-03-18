package org.owasp.netryx.fingerprint.tls.packet.constant;

import org.owasp.netryx.fingerprint.tls.packet.TlsPacket;
import org.owasp.netryx.util.UInts;

import java.util.Arrays;

/**
 * HandshakeType
 * Represents a TLS handshake type
 * <p>
 * CLIENT_HELLO - client hello
 * CLIENT_KEY_EXCHANGE - client key exchange
 * HANDSHAKE_FINISHED - handshake finished
 * SERVER_HELLO - server hello
 * SERVER_CERTIFICATE - server certificate
 * SERVER_KEY_EXCHANGE - server key exchange
 * SERVER_HELLO_DONE - server hello done
 * SERVER_CERTIFICATE_VERIFY - server certificate verify
 * SERVER_NEW_SESSION_TICKET - server new session ticket
 * SERVER_ENCRYPTED_EXTENSIONS - server encrypted extensions
 */
public enum HandshakeType implements TlsPacket {
    CLIENT_HELLO(0x01),
    CLIENT_KEY_EXCHANGE(0x10),
    HANDSHAKE_FINISHED(0x14),
    SERVER_HELLO(0x02),
    SERVER_CERTIFICATE(0x0B),
    SERVER_KEY_EXCHANGE(0x0C),
    SERVER_HELLO_DONE(0x0E),
    SERVER_CERTIFICATE_VERIFY(0x0F),
    SERVER_NEW_SESSION_TICKET(0x04),
    SERVER_ENCRYPTED_EXTENSIONS(0x08);

    private final int id;

    HandshakeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public byte[] toByteArray() {
        return UInts.toUInt8(id);
    }

    public static HandshakeType of(int uint8) {
        return Arrays.stream(values()).filter(c -> c.id == uint8).findFirst().orElseThrow();
    }
}
