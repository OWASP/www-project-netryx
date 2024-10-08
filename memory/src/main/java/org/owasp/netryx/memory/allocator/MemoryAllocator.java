package org.owasp.netryx.memory.allocator;

import org.owasp.netryx.memory.SecureMemory;
import org.owasp.netryx.memory.obfuscator.MemoryObfuscator;
import org.owasp.netryx.memory.obfuscator.XorMemoryObfuscator;

/**
 * Base interface for memory allocators.
 */
public interface MemoryAllocator {
    SecureMemory allocate(int size, MemoryObfuscator obfuscator);

    default SecureMemory allocate(int size) {
        return allocate(size, new XorMemoryObfuscator(size));
    }
}
