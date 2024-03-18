package org.owasp.memory.allocator;

import org.owasp.memory.SecureMemory;

/**
 * Base interface for memory allocators.
 */
public interface MemoryAllocator {
    SecureMemory allocate(int size);
}
