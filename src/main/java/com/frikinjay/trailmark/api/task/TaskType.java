package com.frikinjay.trailmark.api.task;

import com.frikinjay.trailmark.api.quest.Icon;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Describes one kind of task: its id (the {@code "type"} in quest files), how its fields are read and
 * written, and what the editor offers when someone adds a new one.
 * <pre>{@code
 * public static final TaskType<ManaTask> MANA = TaskType.of(
 *         ResourceLocation.fromNamespaceAndPath("mymod", "mana"), ManaTask.CODEC, () -> new ManaTask(100));
 * TrailmarkApi.registerTaskType(MANA);
 * }</pre>
 * Register during mod init, on both sides, before any world loads.
 *
 * @param id      the type id
 * @param codec   reads and writes the type's own fields
 * @param factory makes a sensible default task for the editor's "add task" menu
 * @param icon    icon shown for the type in the editor
 */
public record TaskType<T extends Task>(ResourceLocation id, MapCodec<T> codec, Supplier<T> factory, Icon icon) {

    public static <T extends Task> TaskType<T> of(ResourceLocation id, MapCodec<T> codec, Supplier<T> factory) {
        return new TaskType<>(id, codec, factory, Icon.EMPTY);
    }

    public TaskType<T> withIcon(Icon newIcon) {
        return new TaskType<>(id, codec, factory, newIcon);
    }

    /**
     * The name shown in the editor, from the lang key {@code trailmark.task_type.<namespace>.<path>}.
     */
    public Component displayName() {
        return Component.translatable("trailmark.task_type." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
    }
}
