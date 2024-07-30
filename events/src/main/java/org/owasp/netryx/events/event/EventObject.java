package org.owasp.netryx.events.event;

import org.owasp.netryx.events.event.cancellable.Cancellable;
import org.owasp.netryx.events.marker.Event;

public class EventObject<T extends Event> {
    protected final T event;
    protected final String eventName;
    protected boolean cancelled = false;

    public EventObject(T event) {
        this.event = event;

        if (event instanceof Cancellable) {
            cancelled = ((Cancellable) event).isCancelled();
        }

        eventName = event.getClass().getTypeName();
    }

    public T getEvent() {
        return event;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
