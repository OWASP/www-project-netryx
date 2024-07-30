package org.owasp.netryx.events.handler;

import org.owasp.netryx.events.event.EventPriority;
import org.owasp.netryx.events.marker.Event;

public interface Handler<T extends Event> extends Comparable<Handler<T>> {
    void execute(T event);

    EventPriority getPriority();

    boolean isIgnoreCancelled();
}