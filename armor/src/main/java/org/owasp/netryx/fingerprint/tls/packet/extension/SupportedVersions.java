package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.fingerprint.tls.packet.constant.ProtocolVersion;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class SupportedVersions extends TlsExtension {
    private final List<ProtocolVersion> versions = new ArrayList<>();

    public SupportedVersions() {
        super(ExtensionType.SUPPORTED_VERSIONS.getId(), ExtensionType.SUPPORTED_VERSIONS);
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            in.skip(2);

            var length = in.readUInt8();

            if (length % 2 != 0)
                throw new IllegalStateException("Invalid length for supported versions");

            var totalVersions = length / 2;

            for (var i = 0; i < totalVersions; i++)
                versions.add(ProtocolVersion.of(in.readUInt16()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt16(ExtensionType.SUPPORTED_VERSIONS.getId());

            var versionsLength = versions.size() * 2;
            var length = versionsLength + 1;

            out.writeUInt16(length);
            out.writeUInt8(versionsLength);

            for (var protocol : versions)
                out.writeUInt16(protocol.getId());

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<ProtocolVersion> getVersions() {
        return versions;
    }
}
