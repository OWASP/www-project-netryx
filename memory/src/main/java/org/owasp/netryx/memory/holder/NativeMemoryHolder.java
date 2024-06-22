package org.owasp.netryx.memory.holder;

import com.sun.jna.Native;
import org.owasp.netryx.memory.access.NativeMemory;

/**
 * NativeMemoryHolder
 * Holder for native memory access
 */
public final class NativeMemoryHolder {
    private NativeMemoryHolder() {}

    private static class LazyHolder {
        static final NativeMemory INSTANCE = Native.load("c", NativeMemory.class);
    }

    public static NativeMemory getInstance() {
        return LazyHolder.INSTANCE;
    }
}
