package org.owasp.netryx.events.event.holder;

import org.owasp.netryx.events.marker.Event;

import java.util.List;

public interface EventHolder {
    List<Class<? extends Event>> getHandledEvents();
}