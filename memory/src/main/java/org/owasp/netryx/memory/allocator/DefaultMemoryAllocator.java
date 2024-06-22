package org.owasp.netryx.memory.allocator;

import org.owasp.netryx.memory.SecureMemory;

public class DefaultMemoryAllocator implements MemoryAllocator {
    @Override
    public SecureMemory allocate(int size) {
        return new SecureMemory(size);
    }
}
