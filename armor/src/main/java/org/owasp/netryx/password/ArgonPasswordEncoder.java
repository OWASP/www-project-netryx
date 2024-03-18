package org.owasp.netryx.password;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.owasp.netryx.password.config.ArgonConfig;
import org.owasp.netryx.password.constant.EncoderType;
import org.owasp.netryx.util.BytesUtil;

import java.util.Base64;

import static org.owasp.netryx.password.constant.Constant.STRING_FORMAT;
import static org.owasp.netryx.util.BytesUtil.charsToBytes;

/**
 * ArgonPasswordEncoder
 * Powerful password encoder defined by Alex Biryukov, Daniel Dinu, and Dmitry Khovratovich
 * <p>
 * Winner of the Password Hashing Competition in July 2015
 */
public class ArgonPasswordEncoder implements PasswordEncoder {
    private final ArgonConfig config;

    public ArgonPasswordEncoder(ArgonConfig config) {
        this.config = config;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        var salt = BytesUtil.nextBytes(config.getSaltLength());
        var hash = encode(rawPassword, salt);

        var encodedHash = Base64.getEncoder().encodeToString(hash);
        var encodedSalt = Base64.getEncoder().encodeToString(salt);

        return String.format(STRING_FORMAT, encodedHash, encodedSalt);
    }

    @Override
    public byte[] encode(CharSequence rawPassword, byte[] salt) {
        var generator = new Argon2BytesGenerator();
        generator.init(newArgonParams(salt));

        var hash = new byte[config.getHashLength()];
        generator.generateBytes(charsToBytes(rawPassword), hash);

        return hash;
    }

    private Argon2Parameters newArgonParams(byte[] salt) {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withMemoryAsKB(config.getMemoryCost())
                .withParallelism(config.getParallelism())
                .withIterations(config.getIterations())
                .withSalt(salt)
                .build();
    }

    @Override
    public EncoderType type() {
        return EncoderType.ARGON;
    }
}
