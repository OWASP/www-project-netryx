package org.owasp.memory.util;

import org.owasp.memory.holder.NativeMemoryHolder;

// Just wrapper class for Memory functions.
public final class MemoryUtil {
    private MemoryUtil() {}

    public static int pageSize() {
        return NativeMemoryHolder.getInstance().getpagesize();
    }
}
