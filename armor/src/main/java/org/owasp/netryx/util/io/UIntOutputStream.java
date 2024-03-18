package org.owasp.netryx.util.io;

import org.owasp.netryx.util.UInts;

import java.io.IOException;

/**
 * UIntOutputStream
 * Represents an output stream of unsigned integers
 */
public interface UIntOutputStream {
    void write(byte[] bytes) throws IOException;

    default void writeUInt8(int n) throws IOException {
        write(UInts.toUInt8(n));
    }

    default void writeUInt16(int n) throws IOException {
        write(UInts.toUInt16(n));
    }

    default void writeUInt24(int n) throws IOException {
        write(UInts.toUInt24(n));
    }

    default void writeUInt32(long n) throws IOException {
        write(UInts.toUInt32(n));
    }

    void writeBytes(byte[] bytes) throws IOException;

    void flush() throws IOException;
}
