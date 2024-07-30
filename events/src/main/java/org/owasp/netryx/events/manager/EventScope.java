package org.owasp.netryx.events.manager;

import org.owasp.netryx.events.event.EventObject;
import org.owasp.netryx.events.event.listener.ConsumerEventListener;
import org.owasp.netryx.events.event.listener.EventListener;
import org.owasp.netryx.events.event.listener.ReflectedEventListener;
import org.owasp.netryx.events.handler.Handler;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.utils.EventConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Class for calling and scheduling events.
 * After you created the listeners, you should register them
 * by calling registerListener(s) function.
 *
 * @see EventDispatcher#call(Event)
 */

public class EventScope implements EventDispatcher {
    protected final List<EventListener> listeners = new ArrayList<>();
    protected ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public <T extends Event> List<Handler<T>> getHandlers(EventObject<T> event) {
        var handlers = new ArrayList<Handler<T>>();

        for (var listener : listeners) {
            handlers.addAll(listener.getEventHandlers(event));
        }

        handlers.sort(null);

        return handlers;
    }

    @Override
    public void registerListener(Listener listener) {
        var reflectedListener = new ReflectedEventListener(listener);

        listeners.add(reflectedListener);
    }

    public void registerEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public <T extends Event> void registerEvent(Class<T> clazz, EventConsumer<T> eventConsumer) {
        var consumerListener = new ConsumerEventListener<>(eventConsumer, clazz);
        listeners.add(consumerListener);
    }

    public <T extends Event> void unregisterEvent(Class<T> clazz) {
        var removeList = listeners.stream()
                .filter(ConsumerEventListener.class::isInstance)
                .filter(e -> (e).getName().equals(clazz.getName()))
                .collect(Collectors.toList());

        listeners.removeAll(removeList);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.removeIf(e -> e.getName().equals(listener.getClass().getTypeName()));
    }

    public void removeListeners() {
        listeners.clear();
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void close() {
        listeners.clear();
        executor.shutdown();
    }
}
