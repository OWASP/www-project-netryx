package org.owasp.memory;

import com.sun.jna.Pointer;
import org.owasp.memory.exception.*;
import org.owasp.memory.holder.NativeMemoryHolder;
import org.owasp.memory.util.MemoryValidator;
import org.owasp.memory.constant.Access;

import java.io.Closeable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.owasp.memory.util.MemoryValidator.*;

/**
 * SecureMemory
 * Secure memory implementation.
 * <p>
 * Secure memory is used to store sensitive data and prevent it from being leaked.
 * <p>
 * Data in secure memory is never swapped to the disk
 * and supports safe obfuscation using XOR encryption.
 * <p>
 * NOTE! Available only on Linux and macOS Systems
 */
public final class SecureMemory implements Closeable {
    private int protectionFlags = -1;
    private boolean closed = false;
    private final Object lock = new Object();

    private final int size;
    private final Pointer memory;

    private byte[] xorKey;

    public SecureMemory(int size) {
        this.size = size;
        this.memory = NativeMemoryHolder.getInstance().valloc(size);

        if (memory == null)
            throw new MemoryAllocateException();

        if (NativeMemoryHolder.getInstance().mlock(memory, size) != 0)
            throw new MemoryLockException();
    }

    public void write(byte[] data) {
        write(data, 0);
    }

    public void protect(int modes) {
        synchronized (lock) {
            ensurePageAligned(size);
            checkClosed();

            protectionFlags = modes;

            if (NativeMemoryHolder.getInstance().mprotect(memory, size, modes) != 0)
                throw new MemoryProtectException();
        }
    }

    public void write(byte[] data, int offset) {
        synchronized (lock) {
            ensureWritable(protectionFlags);

            checkClosed();
            MemoryValidator.ensureCapacity(data.length + offset, size);

            memory.write(offset, data, 0, data.length);
        }
    }

    public byte[] read() {
        return read(0, size);
    }

    public byte[] read(int offset, int length) {
        synchronized (lock) {
            ensureReadable(protectionFlags);
            checkClosed();

            return memory.getByteArray(offset, length);
        }
    }

    public void obfuscate() {
        synchronized (lock) {
            ensureRW(protectionFlags);
            checkClosed();

            this.xorKey = generateXorKey();

            modifyMemory((data, xorKey) -> {
                for (var i = 0; i < data.length; i++) {
                    data[i] ^= xorKey[i];
                }
            }, xorKey);
        }
    }

    public void deobfuscate() {
        synchronized (lock) {
            ensureRW(protectionFlags);
            checkClosed();

            modifyMemory((data, key) -> {
                for (var i = 0; i < data.length; i++) {
                    data[i] ^= key[i];
                }
            }, xorKey);
        }
    }

    public <T> T deobfuscate(int offset, int length, Function<byte[], T> function) {
        synchronized (lock) {
            ensureRW(protectionFlags);

            var data = read(offset, length);
            deobfuscate(data, 0, length);

            try {
                return function.apply(data);
            } finally {
                Arrays.fill(data, (byte) 0);
            }
        }
    }

    public <T> T deobfuscate(Function<byte[], T> function) {
        return deobfuscate(0, size, function);
    }

    public void deobfuscate(byte[] data, int offset, int length) {
        if (xorKey == null)
            return;

        for (var i = offset; i < length + offset; i++)
            data[i] ^= xorKey[i];
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (closed) return;

            destroyXorKey();
            makeWritable();

            memory.clear(size);
            closed = true;

            if (NativeMemoryHolder.getInstance().munlock(memory, size) != 0)
                throw new MemoryUnlockException();
        }
    }

    private void checkClosed() {
        if (closed)
            throw new MemoryClosedException();
    }

    private byte[] generateXorKey() {
        var xorKey = new byte[size];
        new SecureRandom().nextBytes(xorKey);

        return xorKey;
    }

    private void modifyMemory(BiConsumer<byte[], byte[]> modifier, byte[] key) {
        var data = memory.getByteArray(0, size);
        modifier.accept(data, key);
        memory.write(0, data, 0, size);
    }

    private void makeWritable() {
        if (protectionFlags == -1)
            return;

        if ((protectionFlags & Access.WRITE) == 0)
            protect(protectionFlags | Access.WRITE);
    }

    private void destroyXorKey() {
        if (xorKey != null) {
            Arrays.fill(xorKey, (byte) 0);
            xorKey = null;
        }
    }
}
