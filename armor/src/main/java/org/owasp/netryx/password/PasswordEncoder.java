package org.owasp.netryx.password;

import org.owasp.netryx.password.constant.EncoderType;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Password encoder interface
 * Base interface for password encoders
 * <p>
 * @see BCryptPasswordEncoder for BCrypt implementation
 * @see SCryptPasswordEncoder for SCrypt implementation
 * @see ArgonPasswordEncoder for Argon2id implementation
 * <p>
 * These are the most secure password encoders available by Armor
 * Other encoders can be developed by extending this interface, but it is not recommended
 * <p>
 * For new projects, prefer Argon2id password encoding
 */
public interface PasswordEncoder {
    String encode(CharSequence rawPassword);

    byte[] encode(CharSequence rawPassword, byte[] salt);

    default boolean matches(CharSequence rawPassword, String encodedPassword) {
        var parts = encodedPassword.split("\\|");

        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid encoded password");

        var hash = Base64.getDecoder().decode(parts[0]);
        var salt = Base64.getDecoder().decode(parts[1]);

        return MessageDigest.isEqual(hash, encode(rawPassword, salt));
    }

    EncoderType type();
}