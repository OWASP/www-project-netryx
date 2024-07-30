package org.owasp.netryx.events.manager;

import org.owasp.netryx.events.event.EventObject;
import org.owasp.netryx.events.handler.Handler;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventDispatcher extends Closeable {
    void registerListener(Listener listener);

    void removeListener(Listener listener);

    <T extends Event> Collection<Handler<T>> getHandlers(EventObject<T> event);

    default <T extends Event> void call(T event) {
        var eventObject = new EventObject<>(event);

        for (Handler<T> handler : getHandlers(eventObject)) {
            if (eventObject.isCancelled() && !handler.isIgnoreCancelled()) {
                continue;
            }

            handler.execute(event);
        }
    }

    default CompletableFuture<Void> callAsync(Event event) {
        return CompletableFuture.runAsync(() -> call(event), getExecutor());
    }

    default CompletableFuture<Void> scheduleEvent(Event event, long delay) {
        var delayedExec = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, getExecutor());

        return CompletableFuture.runAsync(() -> call(event), delayedExec);
    }

    ExecutorService getExecutor();

    @Override
    default void close() {
        getExecutor().shutdown();
    }
}
