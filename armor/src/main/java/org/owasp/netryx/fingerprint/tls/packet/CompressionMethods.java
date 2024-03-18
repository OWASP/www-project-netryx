package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.fingerprint.tls.packet.constant.CompressionType;
import org.owasp.netryx.fingerprint.tls.packet.model.CompressionMethod;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CompressionMethods
 * Represents a TLS compression methods packet
 */
public final class CompressionMethods implements TlsPacket {
    private List<CompressionMethod> methods = new ArrayList<>();

    public CompressionMethods(UIntInputStream in) {
        var l = in.readUInt8();

        for (int i = 0; i < l; i++) {
            var id = in.readUInt8();
            var type = CompressionType.of(id);

            methods.add(new CompressionMethod(id, type));
        }
    }

    public CompressionMethods(List<CompressionMethod> methods) {
        this.methods = methods;
    }

    public CompressionMethods() {
        this(List.of(new CompressionMethod(CompressionType.NULL.getId(), CompressionType.NULL)));
    }

    public void setMethods(List<CompressionMethod> methods) {
        this.methods = methods;
    }

    public List<CompressionMethod> getMethods() {
        return methods;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt8(methods.size());

            for (var method : methods)
                out.writeUInt8(method.getId());

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
