package org.owasp.netryx.memory.holder;

import com.sun.jna.Native;
import org.owasp.netryx.memory.access.UnixNativeMemory;

/**
 * NativeMemoryHolder
 * Holder for native memory access
 */
public final class NativeMemoryHolder {
    private NativeMemoryHolder() {}

    private static class LazyHolder {
        static final UnixNativeMemory INSTANCE = Native.load("c", UnixNativeMemory.class);
    }

    public static UnixNativeMemory getInstance() {
        return LazyHolder.INSTANCE;
    }
}
