package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.fingerprint.tls.packet.constant.CipherType;
import org.owasp.netryx.fingerprint.tls.packet.model.CipherSuite;
import org.owasp.netryx.util.UInts;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CipherSuites
 * Represents a TLS cipher suites packet
 */
public final class CipherSuites implements TlsPacket {
    private List<CipherSuite> ciphers = new ArrayList<>();

    public CipherSuites(UIntInputStream in) {
        var length = in.readUInt16();

        if (length % 2 != 0) {
            throw new IllegalArgumentException("Ciphers size % 2 != 0");
        }

        for (int i = 0; i < length / 2; i++) {
            var id = in.readUInt16();
            var type = CipherType.of(id);

            ciphers.add(new CipherSuite(id, type));
        }
    }

    public CipherSuites(List<CipherSuite> ciphers) {
        this.ciphers.addAll(ciphers);
    }

    public CipherSuites() {
        this(new ArrayList<>());
    }

    public void setCiphers(List<CipherSuite> ciphers) {
        this.ciphers = ciphers;
    }

    // CipherSuite
    public List<CipherSuite> getCiphers() {
        return ciphers;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var ciphers = UInts.toOneDimensional(this.ciphers.stream().map(CipherSuite::toByteArray)
                    .toArray(byte[][]::new));

            out.writeUInt16(ciphers.length);
            out.writeBytes(ciphers);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
