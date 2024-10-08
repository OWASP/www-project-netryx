package org.owasp.netryx.memory.constant;

/***
 * Protection
 * Constants for memory protection.
 * <p>
 * PROT_NONE: No access.
 * PROT_READ: Read access.
 * PROT_WRITE: Write access.
 * PROT_EXEC: Execute access.
 * <p>
 * Use bitwise OR to combine protection constants.
 * Example: PROT_READ | PROT_WRITE - Read and write access.
 */
public final class Access {
    private Access() {}

    public static final int NONE = 0;
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int EXEC = 4;

    public static class Win {
        public static final int PAGE_NOACCESS = 0x01;
        public static final int PAGE_READONLY = 0x02;
        public static final int PAGE_READWRITE = 0x04;
        public static final int PAGE_EXECUTE = 0x10;
        public static final int PAGE_EXECUTE_READ = 0x20;
        public static final int PAGE_EXECUTE_READWRITE = 0x40;
    }
}
