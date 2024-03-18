package org.owasp.memory;

import org.owasp.memory.access.ProcessControlDarwin;
import org.owasp.memory.access.ProcessControlLinux;
import org.owasp.memory.constant.OS;
import org.owasp.memory.holder.ProcessControlDarwinHolder;
import org.owasp.memory.holder.ProcessControlLinuxHolder;

/**
 * ProcessDump
 * Utility class for allowing and blocking core dumps
 * <p>
 * Blocking core dumps is useful for preventing leaks of sensitive data,
 * during inner security threats.
 * Please use this feature carefully.
 * <p>
 * NOTE! Available only on Linux and macOS Systems
 */
public final class ProcessDump {
    private ProcessDump() {}

    public static void allow() {
        var currentOs = OS.current();

        var result = currentOs == OS.MAC ? allowCoreDumpsDarwin() : currentOs == OS.LINUX
                ? allowCoreDumpsLinux() : -1;

        if (result != 0)
            throw new IllegalStateException("Couldn't allow core dumps");
    }

    public static void block() {
        var currentOs = OS.current();

        var result = currentOs == OS.MAC ? blockCoreDumpsDarwin() : currentOs == OS.LINUX
                ? blockCoreDumpsLinux() : -1;

        if (result != 0) {
            throw new IllegalStateException("Couldn't block core dumps");
        }
    }

    private static int allowCoreDumpsDarwin() {
        return ProcessControlDarwinHolder.getInstance()
                .setrlimit(ProcessControlDarwin.RLIMIT_CORE, ProcessControlDarwinHolder.getOldLimits());
    }

    private static int allowCoreDumpsLinux() {
        return ProcessControlLinuxHolder.getInstance()
                .prctl(ProcessControlLinux.PR_SET_DUMPABLE, 1, 0, 0, 0);
    }

    private static int blockCoreDumpsDarwin() {
        var rlimit = new ProcessControlDarwin.Rlimit();

        rlimit.rlim_cur = 0;
        rlimit.rlim_max = 0;

        return ProcessControlDarwinHolder.getInstance().setrlimit(ProcessControlDarwin.RLIMIT_CORE, rlimit);
    }

    private static int blockCoreDumpsLinux() {
        return ProcessControlLinuxHolder.getInstance()
                .prctl(ProcessControlLinux.PR_SET_DUMPABLE, 0, 0, 0, 0);
    }
}
