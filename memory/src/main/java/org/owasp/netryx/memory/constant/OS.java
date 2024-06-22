package org.owasp.netryx.memory.constant;

/**
 * OS
 * Enum for operating systems.
 */
public enum OS {
    WINDOWS,
    MAC,
    LINUX,
    OTHER;

    public static OS current() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return WINDOWS;
        } else if (os.contains("mac")) {
            return MAC;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return LINUX;
        } else {
            return OTHER;
        }
    }
}