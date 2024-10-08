package org.owasp.netryx.memory.allocator;

import org.owasp.netryx.memory.SecureMemory;
import org.owasp.netryx.memory.UnixMemory;
import org.owasp.netryx.memory.WindowsSecureMemory;
import org.owasp.netryx.memory.constant.OS;
import org.owasp.netryx.memory.obfuscator.MemoryObfuscator;

public class DefaultMemoryAllocator implements MemoryAllocator {
    @Override
    public SecureMemory allocate(int size, MemoryObfuscator obfuscator) {
        switch (OS.current()) {
            case LINUX:
            case MAC:
                return new UnixMemory(size, obfuscator);

            case WINDOWS:
                return new WindowsSecureMemory(size, obfuscator);

            default:
                throw new IllegalStateException("Unsupported OS: " + OS.current());
        }
    }
}
