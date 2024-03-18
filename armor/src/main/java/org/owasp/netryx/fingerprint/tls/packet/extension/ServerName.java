package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.SERVER_NAME;

/**
 *
 */
public class ServerName extends TlsExtension {
    private byte[] hostName;

    public ServerName() {
        super(SERVER_NAME.getId(), SERVER_NAME);
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            var skipped = in.skip(4); // entry length + list length

            if (skipped != 4)
                throw new IllegalArgumentException("Bad ServerName extension");

            var dnsHeader = in.readUInt8();

            if (dnsHeader != 0x00)
                throw new IllegalArgumentException("No DNS header");

            var hostNameLength = in.readUInt16();
            var hostName = new byte[hostNameLength];

            var bytesRead = in.read(hostName, 0, hostNameLength);

            if (bytesRead != hostNameLength) throw new IllegalArgumentException("Bad hostName length");

            this.hostName = hostName;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setHostName(byte[] hostName) {
        this.hostName = hostName;
    }

    public byte[] getHostName() {
        return hostName;
    }

    public String getHostNameString() {
        Objects.requireNonNull(hostName, "HostName should be initialized");

        return new String(hostName, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt16(type.getId());
            out.writeUInt16(hostName.length + 5);
            out.writeUInt16(hostName.length + 3);

            out.writeUInt8(0x00);

            out.writeUInt16(hostName.length);
            out.writeBytes(hostName);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
