package org.owasp.netryx.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash functions utilities
 */
public final class Hash {
    private Hash() {
    }

    public static byte[] sha256(byte[] data) {
        return digest("SHA-256", data);
    }

    public static byte[] md5(byte[] data) {
        return digest("MD5", data);
    }

    private static byte[] digest(String id, byte[] data) {
        try {
            var digest = MessageDigest.getInstance(id);
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}