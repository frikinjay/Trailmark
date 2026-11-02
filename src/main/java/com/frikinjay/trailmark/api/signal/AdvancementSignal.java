package com.frikinjay.trailmark.api.signal;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fired when a player earns an advancement criterion. {@code done} tells you whether that finished the
 * whole advancement. Keyed by the advancement id.
 */
public record AdvancementSignal(ServerPlayer player, AdvancementHolder advancement, String criterion, boolean done) implements Signal {
    @Override
    public Object key() {
        return advancement.id();
    }
}
