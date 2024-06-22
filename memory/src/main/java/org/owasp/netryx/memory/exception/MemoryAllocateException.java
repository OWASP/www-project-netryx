package org.owasp.netryx.memory.exception;

public class MemoryAllocateException extends MemoryException {
    private static final String MESSAGE = "Couldn't allocate memory.";

    public MemoryAllocateException() {
        super(MESSAGE);
    }
}
