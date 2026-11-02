package com.frikinjay.trailmark.api;

import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.reward.RewardInfo;
import com.frikinjay.trailmark.api.reward.RewardType;
import com.frikinjay.trailmark.api.task.Task;
import com.frikinjay.trailmark.api.task.TaskInfo;
import com.frikinjay.trailmark.api.task.TaskType;
import com.frikinjay.trailmark.core.book.TypedCodec;
import com.frikinjay.trailmark.task.UnknownTask;
import com.frikinjay.trailmark.reward.UnknownReward;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The task and reward type lists. They're plain maps rather than vanilla registries on purpose: nothing
 * here is synced as registry data, so a server can have more types than a client without either side
 * refusing to connect. A quest that uses a type the reader doesn't know is kept as is and shown as
 * "needs another mod" instead of breaking the book.
 */
public final class TrailmarkRegistries {

    private static final Map<ResourceLocation, TaskType<?>> TASKS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<ResourceLocation, RewardType<?>> REWARDS = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Reads and writes any task, picking the type from the {@code "type"} field. Bare type names default to
     * the {@code trailmark} namespace.
     */
    public static final Codec<Task> TASK_CODEC = new TypedCodec<>("task", TASKS::get, TaskType::codec, TaskInfo.MAP_CODEC,
            Task::type, TaskType::id, Task::info, Task::attachInfo, UnknownTask::new, UnknownTask.class::isInstance,
            t -> ((UnknownTask) t).raw());

    /**
     * Reads and writes any reward, like {@link #TASK_CODEC}.
     */
    public static final Codec<Reward> REWARD_CODEC = new TypedCodec<>("reward", REWARDS::get, RewardType::codec, RewardInfo.MAP_CODEC,
            Reward::type, RewardType::id, Reward::info, Reward::attachInfo, UnknownReward::new, UnknownReward.class::isInstance,
            r -> ((UnknownReward) r).raw());

    private TrailmarkRegistries() {}

    public static <T extends Task> TaskType<T> register(TaskType<T> type) {
        TaskType<?> old = TASKS.putIfAbsent(type.id(), type);
        if (old != null && old != type) throw new IllegalStateException("Task type " + type.id() + " is already registered");
        return type;
    }

    public static <T extends Reward> RewardType<T> register(RewardType<T> type) {
        RewardType<?> old = REWARDS.putIfAbsent(type.id(), type);
        if (old != null && old != type) throw new IllegalStateException("Reward type " + type.id() + " is already registered");
        return type;
    }

    @Nullable
    public static TaskType<?> task(ResourceLocation id) {
        return TASKS.get(id);
    }

    @Nullable
    public static RewardType<?> reward(ResourceLocation id) {
        return REWARDS.get(id);
    }

    public static Collection<TaskType<?>> tasks() {
        synchronized (TASKS) {
            return java.util.List.copyOf(TASKS.values());
        }
    }

    public static Collection<RewardType<?>> rewards() {
        synchronized (REWARDS) {
            return java.util.List.copyOf(REWARDS.values());
        }
    }
}
