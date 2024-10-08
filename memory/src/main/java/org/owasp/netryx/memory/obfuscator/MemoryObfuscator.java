package org.owasp.netryx.memory.obfuscator;

public interface MemoryObfuscator {
    void obfuscate(byte[] data);

    void deobfuscate(byte[] data);

    void destroy();
}