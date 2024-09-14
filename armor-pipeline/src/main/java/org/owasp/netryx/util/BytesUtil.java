package org.owasp.netryx.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Bytes
 * Utility class for bytes
 */
public final class BytesUtil {
    private BytesUtil() {}

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] nextBytes(int length) {
        var bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);

        return bytes;
    }

    public static byte[] charsToBytes(CharSequence charSequence) {
        var charBuffer = CharBuffer.wrap(charSequence);
        var byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);

        var bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        charBuffer.clear();
        byteBuffer.clear();

        return bytes;
    }

    public static byte[] readBytes(ByteBuf buf) {
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return bytes;
    }

    public static ByteBuf newBuffer(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes);
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
}
