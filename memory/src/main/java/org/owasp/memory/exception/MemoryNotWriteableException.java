package org.owasp.memory.exception;

public class MemoryNotWriteableException extends MemoryException {
    private static final String MESSAGE = "Memory is not writeable";

    public MemoryNotWriteableException() {
        super(MESSAGE);
    }
}
