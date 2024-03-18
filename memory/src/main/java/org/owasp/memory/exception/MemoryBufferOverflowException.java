package org.owasp.memory.exception;

public class MemoryBufferOverflowException extends MemoryException {
    private static final String MESSAGE = "Memory buffer overflow: size: %s, provided: %s";

    public MemoryBufferOverflowException(int size, int provided) {
        super(String.format(MESSAGE, size, provided));
    }
}
