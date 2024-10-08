package org.owasp.netryx.memory.obfuscator;

import java.security.SecureRandom;
import java.util.Arrays;

public class XorMemoryObfuscator implements MemoryObfuscator {
    private byte[] xorKey;

    public XorMemoryObfuscator(int size) {
        xorKey = new byte[size];
        new SecureRandom().nextBytes(xorKey);
    }

    @Override
    public void obfuscate(byte[] data) {
        if (xorKey == null) return;

        for (int i = 0; i < data.length; i++) {
            data[i] ^= xorKey[i];
        }
    }

    @Override
    public void deobfuscate(byte[] data) {
        if (xorKey == null) return;

        for (int i = 0; i < data.length; i++) {
            data[i] ^= xorKey[i];
        }
    }

    @Override
    public void destroy() {
        if (xorKey != null) {
            Arrays.fill(xorKey, (byte) 0);
            xorKey = null;
        }
    }
}