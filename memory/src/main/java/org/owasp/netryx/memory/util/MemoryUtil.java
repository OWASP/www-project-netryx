package org.owasp.netryx.memory.util;

import org.owasp.netryx.memory.holder.NativeMemoryHolder;

// Just wrapper class for Memory functions.
public final class MemoryUtil {
    private MemoryUtil() {}

    public static int pageSize() {
        return NativeMemoryHolder.getInstance().getpagesize();
    }
}
