# Netryx Events
Simple yet powerful library for building event-driven applications.

## Usage
#### Creating an Event
To get started, we first need to create the events that we will handle and a listener class for them.

```java
import org.owasp.netryx.events.marker.Event;

public class MyEvent implements Event {
    private final String sampleData;

    public MyEvent(String sampleData) {
        this.sampleData = sampleData;
    }

    public String getSampleData() {
        return sampleData;
    }
}
```

#### Listener class
After creating the events, we need to declare a listener class where our events will be processed. The class should implement the `Listener` interface.

Each listener class can have an unlimited number of event handlers regardless of their type.
```java
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.annotation.EventHandler;

public class MyListener implements Listener {
    @EventHandler
    public void onMyEvent(MyEvent e) {
        System.out.println("Handling my event: " + e.getSampleData());
    }
}
```

You can also define event listener in a functional way:
```java
public static void main(String[] args) {
    EventScope scope = new EventScope();
    
    scope.registerEvent(MyEvent.class, (e) -> {
        System.out.println("Handling event: " + e.getSampleData());
    });
}
```

#### Calling events
Finally, we need to create an instance of `EventScope`, register the listener, and call our event.
```java
import org.owasp.netryx.events.manager.EventScope;

public class Main {
    public static void main(String[] args) {
        var scope = new EventScope();
        scope.registerListener(new MyListener());
        
        scopa.call(new MyEvent("Hello World!"));
    }
}
```

To call event in a async way, use `callAsync`
```java
CompletableFuture<Void> call = scope.callAsync(new MyEvent(""));
```
___

You can handle the same event in multiple methods.
Event Handler's order can be controlled by priority, an event can extend `Cancellable` to stop further handlers execution.

```java
import org.owasp.netryx.events.marker.Event;
import org.owasp.netryx.events.event.Cancellable;

public class MyEvent implements Event, Cancellable {
    private boolean cancelled = false;
    
    private final String sampleData;

    public MyEvent(String sampleData) {
        this.sampleData = sampleData;
    }

    public String getSampleData() {
        return sampleData;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
```

Now let's try to create 2 event handlers where one cancels the next:
We will set the `EventPriority` for the handlers.

```java
import org.owasp.netryx.events.marker.Listener;
import org.owasp.netryx.events.annotation.EventHandler;

public class MyListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCacnelled = true) // Will be executed last
    public void onMyEvent0(MyEvent e) {
        System.out.println("Will be executed although event is cancelled");
    }

    @EventHandler(priority = EventPriority.NORMAL) // will be executed second
    public void onMyEvent1(MyEvent e) {
        System.out.println("Will not be executed, as event is cancelled");
    }

    @EventHandler(priority = EventPriority.LOWEST) // Will be executed first
    public void onMyEvent2(MyEvent e) {
        System.out.println("Handling my event: " + e.getSampleData);
        e.setCancelled(true);
    }
}
```