package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlpnExtension extends TlsExtension {
    private final List<String> protocols = new ArrayList<>();

    public AlpnExtension() {
        super(ExtensionType.APPLICATION_LAYER_PROTOCOL_NEGOTIATION.getId(), ExtensionType.APPLICATION_LAYER_PROTOCOL_NEGOTIATION);
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            in.skip(2);

            var protocolListLength = in.readUInt16();
            var readBytes = 0;

            while (readBytes < protocolListLength) {
                var protocolNameLength = in.readUInt8();
                readBytes += 1;

                var protocolNameBytes = new byte[protocolNameLength];
                var actualRead = in.read(protocolNameBytes, 0, protocolNameLength);

                if (actualRead != protocolNameLength)
                    throw new IllegalStateException("Could not read the complete protocol name");

                readBytes += protocolNameLength;

                var protocolName = new String(protocolNameBytes);
                protocols.add(protocolName);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var protocolBytes = protocols.stream()
                    .map(String::getBytes)
                    .collect(Collectors.toList());

            var protocolListLength = 0;

            for (var protocol : protocolBytes) {
                protocolListLength += 1;
                protocolListLength += protocol.length;
            }

            var extLength = 2 + protocolListLength;

            out.writeUInt16(ExtensionType.APPLICATION_LAYER_PROTOCOL_NEGOTIATION.getId());

            out.writeUInt16(extLength);
            out.writeUInt16(protocolListLength);

            for (var protocol : protocolBytes) {
                out.writeUInt8(protocol.length);
                out.writeBytes(protocol);
            }

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<String> getProtocols() {
        return protocols;
    }
}
