package org.owasp.netryx.events.event;

/**
 * Event priority
 * Higher value - higher priority
 */
public enum EventPriority {
    LOWEST(4),
    LOW(3),
    NORMAL(2),
    HIGH(1),
    HIGHEST(0);

    private final int value;

    EventPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
