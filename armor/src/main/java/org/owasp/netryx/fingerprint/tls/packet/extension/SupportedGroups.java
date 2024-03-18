package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.constant.NamedGroup;
import org.owasp.netryx.fingerprint.tls.packet.model.EllipticCurve;
import org.owasp.netryx.util.UInts;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.SUPPORTED_GROUPS;

/**
 * SupportedGroups
 * Represents a TLS supported groups (elliptic curves) extension
 */
public class SupportedGroups extends TlsExtension {
    private List<EllipticCurve> curves = new ArrayList<>();

    public SupportedGroups() {
        super(SUPPORTED_GROUPS.getId(), SUPPORTED_GROUPS);
    }

    public SupportedGroups(List<EllipticCurve> curves) {
        this();
        this.curves = curves;
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            var skipped = in.skip(2); //skip total count to curves count

            if (skipped != 2)
                throw new IllegalArgumentException("Bad SupportedGroups extension");

            var curvesCount = in.readUInt16();

            if (curvesCount % 2 != 0)
                throw new IllegalArgumentException("Elliptic curve count should be divisible by 2");

            for (int i = 0; i < curvesCount / 2; i++) {
                var id = in.readUInt16();
                var curve = NamedGroup.of(id);

                curves.add(new EllipticCurve(id, curve));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setCurves(List<EllipticCurve> curves) {
        this.curves = curves;
    }

    public List<EllipticCurve> getCurves() {
        return curves;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var ellipticCurveBytes = UInts.toOneDimensional(curves.stream()
                    .map(EllipticCurve::toByteArray)
                    .toArray(byte[][]::new));

            out.writeUInt16(SUPPORTED_GROUPS.getId());
            out.writeUInt16(ellipticCurveBytes.length + 2);
            out.writeUInt16(ellipticCurveBytes.length);

            out.writeBytes(ellipticCurveBytes);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
