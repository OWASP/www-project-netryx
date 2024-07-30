package org.owasp.netryx.events.event.cancellable;

/**
 * Specifies if your event is cancellable or not.
 */
public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
