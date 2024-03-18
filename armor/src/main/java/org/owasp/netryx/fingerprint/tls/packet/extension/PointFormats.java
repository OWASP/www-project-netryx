package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.constant.ECPointFormatType;
import org.owasp.netryx.fingerprint.tls.packet.model.ECPointFormat;
import org.owasp.netryx.util.UInts;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.EC_POINT_FORMATS;

/**
 * PointFormats
 * Represents a TLS point formats extension
 */
public class PointFormats extends TlsExtension {
    private List<ECPointFormat> pointFormats = new ArrayList<>();

    public PointFormats() {
        super(EC_POINT_FORMATS.getId(), EC_POINT_FORMATS);
    }

    public void setPointFormats(List<ECPointFormat> pointFormats) {
        this.pointFormats = pointFormats;
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            var skipped = in.skip(2);

            if (skipped != 2)
                throw new IllegalArgumentException("Bad Point Formats extension");

            var dataCount = in.readUInt8();

            for (int i = 0; i < dataCount; i++) {
                var id = in.readUInt8();
                var type = ECPointFormatType.of(id);

                pointFormats.add(new ECPointFormat(id, type));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<ECPointFormat> getPointFormats() {
        return pointFormats;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var bytes = UInts.toOneDimensional(pointFormats.stream().map(ECPointFormat::toByteArray)
                    .toArray(byte[][]::new));

            var dataFollows = bytes.length + 1;

            out.writeUInt16(EC_POINT_FORMATS.getId());
            out.writeUInt16(dataFollows);
            out.writeUInt8(bytes.length);
            out.writeBytes(bytes);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
