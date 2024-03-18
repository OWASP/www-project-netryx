package org.owasp.netryx.password;

import org.bouncycastle.crypto.generators.SCrypt;
import org.owasp.netryx.password.config.SCryptConfig;
import org.owasp.netryx.password.constant.EncoderType;
import org.owasp.netryx.util.BytesUtil;

import static org.owasp.netryx.password.constant.Constant.STRING_FORMAT;

/**
 * SCryptPasswordEncoder
 * Powerful password encoder defined by Colin Percival
 */
public class SCryptPasswordEncoder implements PasswordEncoder {
    private final SCryptConfig config;

    public SCryptPasswordEncoder(SCryptConfig config) {
        this.config = config;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        var salt = BytesUtil.nextBytes(config.getSaltLength());
        var hash = encode(rawPassword, salt);

        var encodedHash = BytesUtil.base64(hash);
        var encodedSalt = BytesUtil.base64(salt);

        return String.format(STRING_FORMAT, encodedHash, encodedSalt);
    }

    @Override
    public byte[] encode(CharSequence rawPassword, byte[] salt) {
        return SCrypt.generate(
                BytesUtil.charsToBytes(rawPassword), salt,
                config.getCost(), config.getBlockSize(),
                config.getParallelism(), config.getHashLength()
        );
    }

    @Override
    public EncoderType type() {
        return EncoderType.SCRYPT;
    }
}
