package org.owasp.netryx.fingerprint.tls.packet.model;

import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.fingerprint.tls.packet.extension.TlsExtension;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Raw Extension
 * Represents a raw TLS extension with no parsing
 */
public class Extension extends TlsExtension {
    private byte[] data;

    public Extension(int id, ExtensionType type) {
        super(id, type);
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            var length = in.readUInt16();

            var buffer = new byte[length];
            var bytesRead = in.read(buffer, 0, length);

            if (bytesRead != length)
                throw new IllegalArgumentException("Bad extension provided");

            this.data = buffer;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt16(id);
            out.writeUInt16(data.length);
            out.writeBytes(data);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
