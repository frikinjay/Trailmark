package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Something a player did that tasks might care about. Trailmark fires these from a handful of cheap
 * hooks and only hands them to tasks that are unlocked and unfinished for that player's team, so a pack
 * with thousands of quests costs about the same as a pack with ten.
 * <p>
 * Fire your own with {@code TrailmarkApi.signal(...)}; tasks subscribe with
 * {@link com.frikinjay.trailmark.api.task.TaskSubscriptions#on}.
 */
public interface Signal {

    ServerPlayer player();

    /**
     * A lookup key so the dispatcher can skip tasks that can't possibly match, such as the entity type of a kill.
     * Return {@code null} when there's nothing sensible to key on; only wildcard subscribers get those.
     */
    @Nullable
    default Object key() {
        return null;
    }
}
