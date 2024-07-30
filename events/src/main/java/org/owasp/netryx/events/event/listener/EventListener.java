package org.owasp.netryx.events.event.listener;

import org.owasp.netryx.events.event.EventObject;
import org.owasp.netryx.events.event.holder.EventHolder;
import org.owasp.netryx.events.handler.Handler;
import org.owasp.netryx.events.marker.Event;

import java.util.List;

public interface EventListener extends EventHolder {
    <T extends Event> List<Handler<T>> getEventHandlers(EventObject<T> eventObject);

    String getName();
}
