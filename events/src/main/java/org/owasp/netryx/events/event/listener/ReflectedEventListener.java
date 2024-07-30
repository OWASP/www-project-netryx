package org.owasp.netryx.events.event.listener;

import org.owasp.netryx.events.event.EventObject;
import org.owasp.netryx.events.handler.Handler;
import org.owasp.netryx.events.handler.MethodHandler;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.model.WrappedMethod;
import org.owasp.netryx.events.utils.EventHandlers;

import java.util.*;

public class ReflectedEventListener implements EventListener {
    protected final Listener listener;
    protected final String name;

    private final Map<String, WrappedMethod[]> eventHandlers = new HashMap<>();
    private final List<Class<? extends Event>> eventTypes = new ArrayList<>();

    public ReflectedEventListener(Listener listener) {
        this.listener = listener;
        this.name = listener.getClass().getTypeName();

        var extractedHandlers = EventHandlers.extract(listener);
        this.eventHandlers.putAll(extractedHandlers.eventHandlers());
        this.eventTypes.addAll(extractedHandlers.eventTypes());
    }

    private <T extends Event> boolean isApplicable(WrappedMethod method, EventObject<T> eventObject) {
        var typeParams = method.getTypes();

        if (typeParams.length == 0)
            return false;

        var eventType = eventObject.getEvent().getClass().getTypeName();
        var methodEventType = typeParams[0];

        return eventType.equals(methodEventType);
    }

    private <T extends Event> Handler<T> createMethodHandler(Listener listener, WrappedMethod method, T event) {
        var annotation = method.getAnnotation();

        return new MethodHandler<>(annotation.priority(), annotation.ignoreCancelled(),
                listener, method, event);
    }

    public <T extends Event> List<Handler<T>> getEventHandlers(EventObject<T> eventObject) {
        var methods = eventHandlers.get(eventObject.getEventName());

        if (methods == null) return Collections.emptyList();

        var handlers = new ArrayList<Handler<T>>();

        for (int i = 0; i < methods.length; i++) {
            var method = methods[i];

            if (!isApplicable(method, eventObject)) continue;

            handlers.add(createMethodHandler(listener, method, eventObject.getEvent()));
        }

        return handlers;
    }

    @Override
    public List<Class<? extends Event>> getHandledEvents() {
        return eventTypes;
    }

    public String getName() {
        return name;
    }
}