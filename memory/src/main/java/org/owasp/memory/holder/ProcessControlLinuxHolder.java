package org.owasp.memory.holder;

import com.sun.jna.Native;
import org.owasp.memory.access.ProcessControlLinux;

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