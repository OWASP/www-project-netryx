package org.owasp.netryx.events.model;

import org.owasp.netryx.events.annotation.EventHandler;
import org.owasp.netryx.events.exception.EventExecutionException;
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.marker.Listener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper class for Method for not recalculating unneeded things every time.
 */
public class WrappedMethod {
    protected final Method method;
    protected final MethodHandle handle;

    protected final String[] types;
    protected final String returnType;

    protected final EventHandler annotation;

    public WrappedMethod(Method method) {
        try {
            this.method = method;
            this.handle = MethodHandles.lookup().unreflect(method);

            types = Arrays.stream(method.getParameterTypes()).map(Class::getTypeName)
                    .toArray(String[]::new);

            returnType = method.getReturnType().getTypeName();

            annotation = method.getAnnotation(EventHandler.class);
        } catch (IllegalAccessException e) {
            throw new EventExecutionException(e.getMessage());
        }
    }

    public void invoke(Listener target, Event event) {
        try {
            handle.invoke(target, event);
        } catch (Throwable e) {
            throw new EventExecutionException(e.getMessage());
        }
    }

    public Method getMethod() {
        return method;
    }

    public MethodHandle getHandle() {
        return handle;
    }

    public String[] getTypes() {
        return types;
    }

    public String getReturnType() {
        return returnType;
    }

    public EventHandler getAnnotation() {
        return annotation;
    }

    public Class<?> getParameter(int i) {
        return method.getParameterTypes()[i];
    }
}
