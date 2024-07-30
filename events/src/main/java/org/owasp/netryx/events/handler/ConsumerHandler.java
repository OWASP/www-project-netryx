package org.owasp.netryx.events.handler;

import org.owasp.netryx.events.event.EventPriority;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.utils.EventConsumer;

public class ConsumerHandler<T extends Event> implements Handler<T> {
    private final EventConsumer<T> eventConsumer;

    public ConsumerHandler(EventConsumer<T> eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void execute(T event) {
        eventConsumer.accept(event);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }

    @Override
    public boolean isIgnoreCancelled() {
        return false;
    }

    @Override
    public int compareTo(Handler o) {
        return 0;
    }
}
