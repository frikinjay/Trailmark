package com.frikinjay.trailmark.api.task;

import com.frikinjay.trailmark.api.signal.Signal;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Where a task says which {@link Signal}s it wants. Called once per book load, never per tick.
 */
public interface TaskSubscriptions {

    /**
     * Listen for signals of {@code type} whose {@link Signal#key()} equals {@code key}. Pass a {@code null}
     * key to get every signal of that type; prefer a key whenever you have one, it's much cheaper.
     */
    <S extends Signal> void on(Class<S> type, @Nullable Object key, BiConsumer<TaskContext, S> handler);

    default <S extends Signal> void on(Class<S> type, BiConsumer<TaskContext, S> handler) {
        on(type, null, handler);
    }
}
