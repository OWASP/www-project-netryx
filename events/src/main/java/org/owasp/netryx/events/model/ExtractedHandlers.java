package org.owasp.netryx.events.model;

import org.owasp.netryx.events.marker.Event;

import java.util.List;
import java.util.Map;

public class ExtractedHandlers {
    private final Map<String, WrappedMethod[]> eventHandlers;
    private final List<Class<? extends Event>> eventTypes;

    public ExtractedHandlers(Map<String, WrappedMethod[]> eventHandlers, List<Class<? extends Event>> eventTypes) {
        this.eventHandlers = eventHandlers;
        this.eventTypes = eventTypes;
    }

    public Map<String, WrappedMethod[]> eventHandlers() {
        return eventHandlers;
    }

    public List<Class<? extends Event>> eventTypes() {
        return eventTypes;
    }
}