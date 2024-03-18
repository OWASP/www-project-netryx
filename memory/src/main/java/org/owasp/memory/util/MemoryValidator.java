package org.owasp.memory.util;

import org.owasp.memory.exception.MemoryNotAlignedException;
import org.owasp.memory.exception.MemoryNotReadableException;
import org.owasp.memory.exception.MemoryNotWriteableException;
import org.owasp.memory.constant.Access;

public final class MemoryValidator {
    private MemoryValidator() {}

    public static void ensureWritable(int protectionFlags) {
        if (protectionFlags == -1)
            return;

        if ((protectionFlags & Access.WRITE) == 0)
            throw new MemoryNotWriteableException();
    }

    public static void ensureReadable(int protectionFlags) {
        if (protectionFlags == -1)
            return;

        if ((protectionFlags & Access.READ) == 0)
            throw new MemoryNotReadableException();
    }

    public static void ensureRW(int protectionFlags) {
        ensureReadable(protectionFlags);
        ensureWritable(protectionFlags);
    }

    public static void ensureCapacity(int requiredSize, int size) {
        if (requiredSize > size)
            throw new IllegalArgumentException("Buffer overflow. Required size: " + requiredSize + ", but got: " + size);
    }

    public static void ensurePageAligned(int size) {
        if (size % MemoryUtil.pageSize() != 0)
            throw new MemoryNotAlignedException(size, MemoryUtil.pageSize());
    }

}
