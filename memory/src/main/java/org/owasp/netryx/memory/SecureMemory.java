package org.owasp.netryx.memory;

import java.io.Closeable;
import java.util.function.Function;

public interface SecureMemory extends Closeable {
    void write(byte[] data);

    void write(byte[] data, int offset);

    byte[] read();

    byte[] read(int offset, int length);

    void protect(int modes);

    void obfuscate();

    void deobfuscate();

    <T> T deobfuscate(Function<byte[], T> function);

    <T> T deobfuscate(int offset, int length, Function<byte[], T> function);
}