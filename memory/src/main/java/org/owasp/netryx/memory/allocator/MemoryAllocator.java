package org.owasp.netryx.memory.allocator;

import org.owasp.netryx.memory.SecureMemory;

/**
 * Base interface for memory allocators.
 */
public interface MemoryAllocator {
    SecureMemory allocate(int size);
}
