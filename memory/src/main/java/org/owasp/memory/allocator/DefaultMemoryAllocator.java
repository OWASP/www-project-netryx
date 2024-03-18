package org.owasp.memory.allocator;

import org.owasp.memory.SecureMemory;

public class DefaultMemoryAllocator implements MemoryAllocator {
    @Override
    public SecureMemory allocate(int size) {
        return new SecureMemory(size);
    }
}
