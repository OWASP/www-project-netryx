package org.owasp.netryx.events.utils;

import org.owasp.netryx.events.annotation.EventHandler;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.model.ExtractedHandlers;
import org.owasp.netryx.events.model.WrappedMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public final class EventHandlers {
    private EventHandlers() {}

    @SuppressWarnings("unchecked")
    public static ExtractedHandlers extract(Listener listener) {
        var eventHandlers = new HashMap<String, WrappedMethod[]>();
        var eventTypes = new ArrayList<Class<? extends Event>>();

        var clazz = listener.getClass();

        var methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(EventHandlers::isEventHandler)
                .map(WrappedMethod::new)
                .collect(Collectors.toList());

        for (WrappedMethod method : methods) {
            var args = method.getTypes();

            if (args.length == 0) continue;
            var eventType = args[0];

            var eventMethods = eventHandlers.get(eventType);

            if (eventMethods == null)
                eventMethods = new WrappedMethod[0];

            var eventClass = (Class<? extends Event>) method.getParameter(0);
            eventTypes.add(eventClass);

            var list = new ArrayList<>(Arrays.asList(eventMethods));
            list.add(method);

            eventHandlers.put(eventType, list.toArray(WrappedMethod[]::new));
        }

        return new ExtractedHandlers(eventHandlers, eventTypes);
    }

    private static boolean isEventHandler(Method m) {
        return m.isAnnotationPresent(EventHandler.class);
    }
}
