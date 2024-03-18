package org.owasp.memory.access;

import com.sun.jna.Library;
import com.sun.jna.Structure;

/**
 * ProcessControlDarwin
 * Native process control access using JNA for macOS.
 */
public interface ProcessControlDarwin extends Library {
    int RLIMIT_CORE = 4;

    int getrlimit(int resource, Rlimit rlim);
    int setrlimit(int resource, Rlimit rlim);

    @Structure.FieldOrder({ "rlim_cur", "rlim_max" })
    class Rlimit extends Structure implements Structure.ByReference {
        public long rlim_cur;
        public long rlim_max;
    }
}
