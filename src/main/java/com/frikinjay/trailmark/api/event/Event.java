package com.frikinjay.trailmark.api.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * A dead simple listener list. Register once during mod init; listeners run on the thread that fires the
 * event, which for everything in {@link TrailmarkEvents} is the server thread.
 *
 * @param <T> the listener interface
 */
public final class Event<T> {

    private final List<T> listeners = new CopyOnWriteArrayList<>();
    private final Function<List<T>, T> combiner;
    private volatile T invoker;

    private Event(Function<List<T>, T> combiner) {
        this.combiner = combiner;
        this.invoker = combiner.apply(listeners);
    }

    /**
     * Makes an event whose invoker is built from the current listener list by {@code combiner}.
     */
    public static <T> Event<T> create(Function<List<T>, T> combiner) {
        return new Event<>(combiner);
    }

    public void register(T listener) {
        listeners.add(listener);
        invoker = combiner.apply(listeners);
    }

    public void unregister(T listener) {
        listeners.remove(listener);
        invoker = combiner.apply(listeners);
    }

    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    /**
     * The combined listener. Call its method to fire the event.
     */
    public T invoker() {
        return invoker;
    }
}
