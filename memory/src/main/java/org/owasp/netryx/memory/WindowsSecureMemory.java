package org.owasp.netryx.memory;

import com.sun.jna.Pointer;
import org.owasp.netryx.memory.constant.Access;
import org.owasp.netryx.memory.exception.*;
import org.owasp.netryx.memory.holder.WindowsNativeMemoryHolder;
import org.owasp.netryx.memory.obfuscator.MemoryObfuscator;
import org.owasp.netryx.memory.util.MemoryValidator;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public final class WindowsSecureMemory implements SecureMemory {
    private int protectionFlags = -1;
    private boolean closed = false;
    private final Object lock = new Object();

    private final int size;
    private final Pointer memory;

    private MemoryObfuscator obfuscator;

    public WindowsSecureMemory(int size, MemoryObfuscator obfuscator) {
        this.size = size;
        this.obfuscator = obfuscator;
        this.memory = WindowsNativeMemoryHolder.virtualAlloc(size);

        if (memory == null)
            throw new MemoryAllocateException();

        if (!WindowsNativeMemoryHolder.virtualLock(memory, size))
            throw new MemoryLockException();
    }

    @Override
    public void write(byte[] data) {
        write(data, 0);
    }

    @Override
    public void write(byte[] data, int offset) {
        synchronized (lock) {
            MemoryValidator.ensureWritable(protectionFlags);

            checkClosed();
            MemoryValidator.ensureCapacity(data.length + offset, size);

            memory.write(offset, data, 0, data.length);
        }
    }

    @Override
    public byte[] read() {
        return read(0, size);
    }

    @Override
    public byte[] read(int offset, int length) {
        synchronized (lock) {
            MemoryValidator.ensureReadable(protectionFlags);
            checkClosed();

            return memory.getByteArray(offset, length);
        }
    }

    @Override
    public void protect(int modes) {
        synchronized (lock) {
            checkClosed();

            protectionFlags = modes;

            if (!WindowsNativeMemoryHolder.virtualProtect(memory, size, modes))
                throw new MemoryProtectException();
        }
    }

    @Override
    public void obfuscate() {
        synchronized (lock) {
            MemoryValidator.ensureRW(protectionFlags);
            checkClosed();

            modifyMemory(data -> obfuscator.obfuscate(data));
        }
    }

    @Override
    public void deobfuscate() {
        synchronized (lock) {
            MemoryValidator.ensureRW(protectionFlags);
            checkClosed();

            modifyMemory(data -> obfuscator.deobfuscate(data));
        }
    }

    @Override
    public <T> T deobfuscate(Function<byte[], T> function) {
        return deobfuscate(0, size, function);
    }

    @Override
    public <T> T deobfuscate(int offset, int length, Function<byte[], T> function) {
        synchronized (lock) {
            MemoryValidator.ensureRW(protectionFlags);

            var data = read(offset, length);
            obfuscator.deobfuscate(data);

            try {
                return function.apply(data);
            } finally {
                Arrays.fill(data, (byte) 0);
            }
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (closed) return;

            if (obfuscator != null) {
                obfuscator.destroy();
                obfuscator = null;
            }
            makeWritable();

            memory.clear(size);
            closed = true;

            if (!WindowsNativeMemoryHolder.virtualUnlock(memory, size))
                throw new MemoryUnlockException();

            WindowsNativeMemoryHolder.virtualFree(memory, size);
        }
    }

    private void checkClosed() {
        if (closed)
            throw new MemoryClosedException();
    }

    private void modifyMemory(Consumer<byte[]> modifier) {
        var data = memory.getByteArray(0, size);
        modifier.accept(data);
        memory.write(0, data, 0, size);
    }

    private void makeWritable() {
        if (protectionFlags == -1)
            return;

        if ((protectionFlags & Access.WRITE) == 0)
            protect(protectionFlags | Access.WRITE);
    }
}