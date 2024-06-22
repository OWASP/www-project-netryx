package org.owasp.netryx.memory.test;

import org.junit.jupiter.api.Test;
import org.owasp.netryx.memory.allocator.DefaultMemoryAllocator;
import org.owasp.netryx.memory.constant.Access;
import org.owasp.netryx.memory.exception.MemoryNotReadableException;
import org.owasp.netryx.memory.exception.MemoryNotWriteableException;
import org.owasp.netryx.memory.util.MemoryUtil;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for Allocation
public class MemoryTest {
    private static final String MESSAGE = "Hello world";
    private final DefaultMemoryAllocator allocator = new DefaultMemoryAllocator();

    @Test
    public void assertMemoryOperationsValid() {
        var messageBytes = MESSAGE.getBytes();

        try (var memory = allocator.allocate(messageBytes.length)) {
            memory.write(messageBytes);
            memory.obfuscate();

            assertNotEquals(MESSAGE, new String(memory.read()));

            var deobfuscated = memory.deobfuscate(String::new);
            assertEquals(MESSAGE, deobfuscated);
        }
    }

    @Test
    public void assertMemoryProtected() {
        var messageBytes = MESSAGE.getBytes();

        try (var memory = allocator.allocate(MemoryUtil.pageSize())) {
            memory.write(messageBytes);
            memory.obfuscate();
            memory.protect(Access.READ);

            assertNotEquals(MESSAGE, new String(memory.read(0, messageBytes.length)));
            assertThrows(MemoryNotWriteableException.class, () -> memory.write("Hello".getBytes()));

            memory.protect(Access.WRITE);
            assertThrows(MemoryNotReadableException.class, () -> memory.read(0, messageBytes.length));

            memory.protect(Access.READ | Access.WRITE);

            var deobfuscated = memory.deobfuscate(0, messageBytes.length, String::new);
            assertEquals(MESSAGE, deobfuscated);

            // Remove all access. Testing if access will be reverted on close
            memory.protect(Access.NONE);
        }
    }
}
