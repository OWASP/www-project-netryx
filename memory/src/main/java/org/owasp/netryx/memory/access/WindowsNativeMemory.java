package org.owasp.netryx.memory.access;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface WindowsNativeMemory extends Library {
    Pointer VirtualAlloc(Pointer lpAddress, int dwSize, int flAllocationType, int flProtect);

    boolean VirtualLock(Pointer lpAddress, int dwSize);

    boolean VirtualUnlock(Pointer lpAddress, int dwSize);

    boolean VirtualProtect(Pointer lpAddress, int dwSize, int flNewProtect, IntByReference lpflOldProtect);

    boolean VirtualFree(Pointer lpAddress, int dwSize, int dwFreeType);
}