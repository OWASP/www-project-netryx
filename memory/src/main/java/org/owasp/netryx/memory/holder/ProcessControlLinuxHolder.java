package org.owasp.netryx.memory.holder;

import com.sun.jna.Native;
import org.owasp.netryx.memory.access.ProcessControlLinux;

/**
 * ProcessControlLinuxHolder
 * Holder for native process control access for Linux.
 */
public final class ProcessControlLinuxHolder {
    private ProcessControlLinuxHolder() {}

    private static class LazyHolder {
        static final ProcessControlLinux INSTANCE = Native.load("c", ProcessControlLinux.class);
    }

    public static ProcessControlLinux getInstance() {
        return LazyHolder.INSTANCE;
    }
}