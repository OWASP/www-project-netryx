package org.owasp.netryx.memory.exception;

public class MemoryLockException extends MemoryException {
    private static final String MESSAGE = "Couldn't lock memory region.";

    public MemoryLockException() {
        super(MESSAGE);
    }
}
