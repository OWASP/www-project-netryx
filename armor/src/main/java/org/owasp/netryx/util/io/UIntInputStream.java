package org.owasp.netryx.util.io;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * UIntInputStream
 * Represents an input stream of unsigned integers
 */
public interface UIntInputStream {
    int read() throws IOException;

    default int readUInt8() {
        try {
            return read() & 0x00FF;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    default int readUInt16() {
        try {
            return (read() << 8) | readUInt8();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    default int readUInt24() {
        try {
            return (read() << 16) | (read() << 8) | (read() & 0x00FF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    default long readUInt32() {
        try {
            return ((long) read() << 24) | ((long) read() << 16) | ((long) read() << 8) | (read() & 0x00FF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    long skip(long i) throws IOException;

    int read(byte[] dest, int off, int size) throws IOException;
}
