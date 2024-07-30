package org.owasp.netryx.events.utils;

import org.owasp.netryx.events.marker.Event;

public interface EventConsumer<T extends Event> {
    void accept(T event);
}
