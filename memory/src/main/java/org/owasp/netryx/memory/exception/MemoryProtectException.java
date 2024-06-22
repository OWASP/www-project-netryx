package org.owasp.netryx.memory.exception;

public class MemoryProtectException extends MemoryException {
    private static final String MESSAGE = "Couldn't protect memory region.";

    public MemoryProtectException() {
        super(MESSAGE);
    }
}
