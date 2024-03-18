package org.owasp.memory.exception;

public class MemoryNotAlignedException extends MemoryException {
    private static final String MESSAGE = "Memory size (%s) should be a multiple of the page size (%s)";

    public MemoryNotAlignedException(int size, int pageSize) {
        super(String.format(MESSAGE, size, pageSize));
    }
}
