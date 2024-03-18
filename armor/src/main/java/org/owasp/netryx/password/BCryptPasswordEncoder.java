package org.owasp.netryx.password;

import org.bouncycastle.crypto.generators.BCrypt;
import org.owasp.netryx.password.config.BCryptConfig;
import org.owasp.netryx.password.constant.EncoderType;
import org.owasp.netryx.util.BytesUtil;

import static org.owasp.netryx.password.constant.Constant.STRING_FORMAT;
import static org.owasp.netryx.util.BytesUtil.charsToBytes;

/**
 * BCryptPasswordEncoder
 * Well-known password encoder defined by Niels Provos and David Mazières
 */
public class BCryptPasswordEncoder implements PasswordEncoder {
    private final BCryptConfig config;

    public BCryptPasswordEncoder(BCryptConfig config) {
        this.config = config;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        var salt = BytesUtil.nextBytes(16);
        var hash = encode(rawPassword, salt);

        var encodedHash = BytesUtil.base64(hash);
        var encodedSalt = BytesUtil.base64(salt);

        return String.format(STRING_FORMAT, encodedHash, encodedSalt);
    }

    @Override
    public byte[] encode(CharSequence rawPassword, byte[] salt) {
        return BCrypt.generate(charsToBytes(rawPassword), salt, config.getCost());
    }

    @Override
    public EncoderType type() {
        return EncoderType.BCRYPT;
    }
}
