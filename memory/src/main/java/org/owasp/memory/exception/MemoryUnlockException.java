package org.owasp.memory.exception;

public class MemoryUnlockException extends MemoryException {
    private static final String MESSAGE = "Couldn't unlock memory region.";

    public MemoryUnlockException() {
        super(MESSAGE);
    }
}
