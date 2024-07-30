package org.owasp.netryx.events.event.listener;

import org.owasp.netryx.events.event.EventObject;
import org.owasp.netryx.events.handler.ConsumerHandler;
import org.owasp.netryx.events.handler.Handler;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.utils.EventConsumer;

import java.util.Collections;
import java.util.List;

public class ConsumerEventListener<E extends Event> implements EventListener {
    private final EventConsumer<E> eventConsumer;
    private final Class<E> clazz;

    public ConsumerEventListener(EventConsumer<E> eventConsumer, Class<E> clazz) {
        this.eventConsumer = eventConsumer;
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> List<Handler<T>> getEventHandlers(EventObject<T> eventObject) {
        var event = eventObject.getEvent();

        if (!clazz.isInstance(event)) {
            return Collections.emptyList();
        }

        var handler = (ConsumerHandler<T>) new ConsumerHandler<>(eventConsumer);
        return Collections.singletonList(handler);
    }

    @Override
    public List<Class<? extends Event>> getHandledEvents() {
        return List.of(clazz);
    }

    @Override
    public String getName() {
        return clazz.getName();
    }
}
