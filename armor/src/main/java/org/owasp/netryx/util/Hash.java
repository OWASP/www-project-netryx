package org.owasp.netryx.util;

import org.bouncycastle.jcajce.provider.digest.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * Hash functions utilities
 */
public final class Hash {
    private Hash() {
    }

    public static byte[] sha256(byte[] data) {
        var digest = new SHA256.Digest();

        return digest.digest(data);
    }

    public static byte[] doubleSha256(byte[] data) {
        return sha256(sha256(data));
    }

    public static byte[] ripeMd160(byte[] data) {
        var digest = new RIPEMD160.Digest();
        return digest.digest(data);
    }

    public static byte[] hash160(byte[] data) {
        return ripeMd160(sha256(data));
    }

    public static byte[] checksum(byte[] data) {
        var hash = doubleSha256(data);
        return Arrays.copyOfRange(hash, 0, 4);
    }

    public static byte[] md5(byte[] data) {
        var digest = new MD5.Digest();
        return digest.digest(data);
    }

    public static byte[] sha3(byte[] data) {
        var digest = new SHA3.Digest256();
        digest.update(data, 0, data.length);

        return digest.digest();
    }

    public static byte[] keccak256(byte[] data) {
        var digest = new Keccak.Digest256();
        digest.update(data);

        return digest.digest();
    }

    public static byte[] hmacSha512(byte[] key, byte[] data) {
        try {
            var hmacSha512 = Mac.getInstance("HmacSHA512");
            var secretKey = new SecretKeySpec(key, "HmacSHA512");
            hmacSha512.init(secretKey);

            return hmacSha512.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate hmac-sha512", e);
        }
    }
}