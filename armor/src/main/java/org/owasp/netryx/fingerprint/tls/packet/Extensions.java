package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.exception.TlsException;
import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.fingerprint.tls.packet.extension.TlsExtension;
import org.owasp.netryx.fingerprint.tls.packet.model.Extension;
import org.owasp.netryx.util.UInts;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntDataInputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.UNKNOWN;

@SuppressWarnings("unused")
public final class Extensions implements TlsPacket {
    private List<TlsExtension> extensions = new ArrayList<>();

    public Extensions(UIntInputStream in)  {
        try {
            var size = in.readUInt16();

            if (size == -1) return; //no extensions.

            var buf = new byte[size];
            var read = in.read(buf, 0, size);

            if (read != size)
                throw new IllegalArgumentException("Bad extensions provided");

            try (var extin = new UIntDataInputStream(buf)) {
                while (extin.available() > 0) {
                    var id = extin.readUInt16();
                    var extensionType = ExtensionType.of(id);

                    var extension = (extensionType == UNKNOWN) ? new Extension(id, UNKNOWN)
                            : extensionType.getExtension();

                    extension.read(extin);

                    extensions.add(extension);
                }
            }
        } catch (IOException e) {
            throw new TlsException(e);
        }
    }

    public Extensions(List<TlsExtension> extensions) {
        this.extensions = extensions;
    }

    public Extensions() {
        this(new ArrayList<>());
    }

    public List<TlsExtension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<TlsExtension> extensions) {
        this.extensions = extensions;
    }

    public <T extends TlsExtension> T getExtension(ExtensionType type, Class<T> clazz) {
        var extension = extensions.stream()
                .filter(e -> e.getType() == type)
                .findFirst()
                .orElse(null);

        if (extension == null) return null; //no such extension

        return clazz.cast(extension);
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var bytes = UInts.toOneDimensional(extensions.stream().map(TlsPacket::toByteArray)
                    .toArray(byte[][]::new));

            out.writeUInt16(bytes.length);
            out.writeBytes(bytes);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}