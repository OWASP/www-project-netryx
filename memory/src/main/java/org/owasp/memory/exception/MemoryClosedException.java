package org.owasp.memory.exception;

public class MemoryClosedException extends MemoryException {
    private static final String MESSAGE = "Memory is closed.";

    public MemoryClosedException() {
        super(MESSAGE);
    }
}
