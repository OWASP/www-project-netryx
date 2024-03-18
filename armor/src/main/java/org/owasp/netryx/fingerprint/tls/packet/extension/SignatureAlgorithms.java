package org.owasp.netryx.fingerprint.tls.packet.extension;

import org.owasp.netryx.fingerprint.tls.packet.constant.SignAlgorithm;
import org.owasp.netryx.fingerprint.tls.packet.model.SignatureAlgorithm;
import org.owasp.netryx.util.UInts;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.owasp.netryx.fingerprint.tls.packet.constant.ExtensionType.SIGNATURE_ALGORITHMS;

/**
 * SignatureAlgorithms
 * Represents a TLS signature algorithms extension
 */
public class SignatureAlgorithms extends TlsExtension {
    private List<SignatureAlgorithm> algorithms = new ArrayList<>();

    public SignatureAlgorithms() {
        super(SIGNATURE_ALGORITHMS.getId(), SIGNATURE_ALGORITHMS);
    }

    public SignatureAlgorithms(List<SignatureAlgorithm> algorithms) {
        this();
        this.algorithms = algorithms;
    }

    @Override
    public void read(UIntInputStream in) {
        try {
            var skipped = in.skip(2);

            if (skipped != 2)
                throw new IllegalArgumentException("Bad signature algorithms");

            var follows = in.readUInt16();

            if (follows % 2 != 0)
                throw new IllegalArgumentException("Signature algorithms should be divisible by 2");

            for (int i = 0; i < follows / 2; i++) {
                var id = in.readUInt16();
                var type = SignAlgorithm.of(id);

                algorithms.add(new SignatureAlgorithm(id, type));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<SignatureAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<SignatureAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            var signAlgorithmBytes = UInts.toOneDimensional(algorithms.stream().map(SignatureAlgorithm::toByteArray)
                    .toArray(byte[][]::new));

            out.writeUInt16(SIGNATURE_ALGORITHMS.getId());
            out.writeUInt16(signAlgorithmBytes.length + 2);
            out.writeUInt16(signAlgorithmBytes.length);

            out.writeBytes(signAlgorithmBytes);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
