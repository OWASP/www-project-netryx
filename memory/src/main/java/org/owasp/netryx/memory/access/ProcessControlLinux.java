package org.owasp.netryx.memory.access;

import com.sun.jna.Library;

/**
 * ProcessControlLinux
 * Native process control access using JNA for Linux.
 */
public interface ProcessControlLinux extends Library {
    int PR_SET_DUMPABLE = 3;

    int prctl(int option, long arg2, long arg3, long arg4, long arg5);
}