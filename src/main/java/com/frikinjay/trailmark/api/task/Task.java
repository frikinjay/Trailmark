package com.frikinjay.trailmark.api.task;

import com.frikinjay.trailmark.api.quest.Icon;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * One objective inside a quest. Subclass it, give it a {@link TaskType}, and either subscribe to signals
 * in {@link #subscribe} or override {@link #submit} for tasks the player hands in by clicking.
 * <p>
 * Tasks are immutable definitions shared by every team. Never store progress on the task itself; use the
 * {@link TaskContext} you're given.
 */
public abstract class Task {

    private TaskInfo info = TaskInfo.DEFAULT;

    public abstract TaskType<?> type();

    /**
     * The number progress has to reach. Most tasks are 1 (done or not).
     */
    public long target() {
        return 1;
    }

    /**
     * Register the signals this task listens to. The default does nothing, which suits tasks that only
     * complete through {@link #submit} or through commands.
     */
    public void subscribe(TaskSubscriptions subscriptions) {
    }

    /**
     * Whether the quest screen shows a hand-in button for this task.
     */
    public boolean canSubmit() {
        return false;
    }

    /**
     * Called on the server when the player presses the hand-in button. Update progress through the context.
     */
    public void submit(TaskContext context, ServerPlayer player) {
    }

    /**
     * The generated title, used when the quest file doesn't set one.
     */
    public abstract Component defaultTitle();

    /**
     * The generated icon, used when the quest file doesn't set one.
     */
    public Icon defaultIcon() {
        return type().icon();
    }

    /**
     * The item this task is about, for its item tooltip and recipe lookups in the quest book; empty when the task
     * isn't about one item (its icon is then just a picture).
     */
    public net.minecraft.world.item.ItemStack subjectItem() {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    /**
     * Extra tooltip lines under the task's title in the quest book, such as what an advancement asks for.
     */
    public List<Component> tooltip() {
        return List.of();
    }

    /**
     * How progress is shown, such as {@code "3 / 8"}, {@code "250 / 1000 mB"} or {@code "2m 30s"}.
     */
    public Component formatProgress(long progress) {
        return Component.literal(Math.min(progress, target()) + " / " + target());
    }

    public final TaskInfo info() {
        return info;
    }

    public final String id() {
        return info.id();
    }

    public final boolean isOptional() {
        return info.optional();
    }

    public final Component title() {
        return info.title().isEmpty() ? defaultTitle() : Component.literal(info.title());
    }

    public final Icon icon() {
        return info.icon().or(defaultIcon());
    }

    @ApiStatus.Internal
    public final void attachInfo(TaskInfo newInfo) {
        this.info = newInfo;
    }
}
