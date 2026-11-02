package com.frikinjay.trailmark.api.task;

import com.frikinjay.trailmark.api.quest.Quest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Handed to a task when something it subscribed to happens. Progress is stored per team, so every team
 * member's actions count towards the same number.
 */
public interface TaskContext {

    MinecraftServer server();

    /**
     * The player whose action caused this, or {@code null} for team-wide updates such as commands.
     */
    @Nullable
    ServerPlayer player();

    UUID teamId();

    Quest quest();

    Task task();

    /**
     * Current progress, between 0 and {@link Task#target()}.
     */
    long progress();

    /**
     * Sets progress, clamped to the task's target. Reaching the target completes the task.
     */
    void setProgress(long value);

    default void addProgress(long amount) {
        if (amount != 0) setProgress(progress() + amount);
    }

    default void complete() {
        setProgress(task().target());
    }

    default boolean isComplete() {
        return progress() >= task().target();
    }
}
