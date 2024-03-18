package org.owasp.memory.access;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * NativeMemory
 * Native memory access using JNA.
 */
public interface NativeMemory extends Library {
    int mlock(Pointer addr, int len);

    int munlock(Pointer addr, int len);

    int mprotect(Pointer addr, int len, int prot);

    Pointer valloc(int size);

    int getpagesize();
}
