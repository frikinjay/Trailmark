package com.frikinjay.trailmark.api.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a player right clicks a block or an entity. Keyed by the block or the entity type.
 *
 * @param block  the clicked block, or {@code null} for entities
 * @param pos    where the block is, or {@code null} for entities
 * @param entity the clicked entity, or {@code null} for blocks
 * @param held   what the player held in {@code hand}
 */
public record InteractSignal(ServerPlayer player, @Nullable BlockState block, @Nullable BlockPos pos,
                             @Nullable Entity entity, InteractionHand hand, ItemStack held) implements Signal {
    @Override
    public Object key() {
        return block != null ? block.getBlock() : entity != null ? entity.getType() : null;
    }
}
