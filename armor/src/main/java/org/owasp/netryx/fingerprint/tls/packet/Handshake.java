package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.fingerprint.tls.packet.constant.HandshakeType;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Handshake
 * Represents a TLS handshake packet
 */
public final class Handshake implements TlsPacket {
    private int messageLength;

    private final HandshakeType handshakeType;

    public Handshake(UIntInputStream in) {
        var messageType = in.readUInt8();

        try {
            this.handshakeType = HandshakeType.of(messageType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown header for handshake");
        }

        this.messageLength = in.readUInt24();
    }

    public Handshake(HandshakeType handshakeType) {
        this.handshakeType = handshakeType;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public HandshakeType getHandshakeType() {
        return handshakeType;
    }

    public int getMessageLength() {
        return messageLength;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt8(handshakeType.getId());
            out.writeUInt24(messageLength);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
