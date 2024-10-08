package org.owasp.netryx.memory.holder;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.owasp.netryx.memory.access.WindowsNativeMemory;

public final class WindowsNativeMemoryHolder {
    private WindowsNativeMemoryHolder() {}

    private static class LazyHolder {
        static final WindowsNativeMemory INSTANCE = Native.load("kernel32", WindowsNativeMemory.class);
    }

    public static WindowsNativeMemory getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static Pointer virtualAlloc(int size) {
        // MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE
        return getInstance().VirtualAlloc(null, size, 0x1000 | 0x2000, 0x04);
    }

    public static boolean virtualLock(Pointer address, int size) {
        return getInstance().VirtualLock(address, size);
    }

    public static boolean virtualUnlock(Pointer address, int size) {
        return getInstance().VirtualUnlock(address, size);
    }

    public static boolean virtualProtect(Pointer address, int size, int newProtect) {
        var oldProtect = new IntByReference();
        return getInstance().VirtualProtect(address, size, newProtect, oldProtect);
    }

    public static boolean virtualFree(Pointer address, int size) {
        return getInstance().VirtualFree(address, 0, 0x8000);
    }
}