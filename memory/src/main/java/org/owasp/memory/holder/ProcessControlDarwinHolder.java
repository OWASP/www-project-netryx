package org.owasp.memory.holder;

import com.sun.jna.Native;
import org.owasp.memory.access.ProcessControlDarwin;

/**
 * ProcessControlDarwinHolder
 * Holder for native process control access for macOS.
 */
public final class ProcessControlDarwinHolder {
    private ProcessControlDarwinHolder() {}

    private static class LazyHolder {
        static final ProcessControlDarwin INSTANCE = Native.load("c", ProcessControlDarwin.class);
        static final ProcessControlDarwin.Rlimit OLD_LIMITS = new ProcessControlDarwin.Rlimit();

        static {
            var res = INSTANCE.getrlimit(ProcessControlDarwin.RLIMIT_CORE, OLD_LIMITS);

            if (res != 0)
                throw new IllegalStateException("Couldn't get current core limits");
        }
    }

    public static ProcessControlDarwin getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static ProcessControlDarwin.Rlimit getOldLimits() {
        return LazyHolder.OLD_LIMITS;
    }
}
