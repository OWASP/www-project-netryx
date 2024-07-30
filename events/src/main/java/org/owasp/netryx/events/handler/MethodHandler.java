package org.owasp.netryx.events.handler;

import org.owasp.netryx.events.event.EventPriority;
import org.owasp.netryx.events.exception.EventExecutionException;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.model.WrappedMethod;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class MethodHandler<T extends Event> implements Handler<T> {
    protected final EventPriority priority;
    protected final boolean ignoreCancelled;

    protected final Listener target;
    protected final WrappedMethod wrappedMethod;
    protected final T event;

    public MethodHandler(EventPriority priority, boolean ignoreCancelled, Listener target, WrappedMethod method, T event) {
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.target = target;
        this.wrappedMethod = method;
        this.event = event;
    }

    @Override
    public void execute(T event) {
        try {
            wrappedMethod.invoke(target, event);
        } catch (Exception e) {
            throw new EventExecutionException(e.getMessage());
        }
    }

    @Override
    public EventPriority getPriority() {
        return priority;
    }

    @Override
    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public int compareTo(Handler handler) {
        return Integer.compare(priority.getValue(), handler.getPriority().getValue());
    }

    public Listener getTarget() {
        return target;
    }

    public WrappedMethod getWrappedMethod() {
        return wrappedMethod;
    }

    public T getEvent() {
        return event;
    }
}
