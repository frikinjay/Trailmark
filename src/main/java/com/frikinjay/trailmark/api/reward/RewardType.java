package com.frikinjay.trailmark.api.reward;

import com.frikinjay.trailmark.api.quest.Icon;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Describes one kind of reward. Works exactly like {@link com.frikinjay.trailmark.api.task.TaskType}.
 *
 * @param id      the type id, used as {@code "type"} in quest files
 * @param codec   reads and writes the type's own fields
 * @param factory default reward for the editor
 * @param icon    icon shown for the type in the editor
 */
public record RewardType<T extends Reward>(ResourceLocation id, MapCodec<T> codec, Supplier<T> factory, Icon icon) {

    public static <T extends Reward> RewardType<T> of(ResourceLocation id, MapCodec<T> codec, Supplier<T> factory) {
        return new RewardType<>(id, codec, factory, Icon.EMPTY);
    }

    public RewardType<T> withIcon(Icon newIcon) {
        return new RewardType<>(id, codec, factory, newIcon);
    }

    public Component displayName() {
        return Component.translatable("trailmark.reward_type." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
    }
}
