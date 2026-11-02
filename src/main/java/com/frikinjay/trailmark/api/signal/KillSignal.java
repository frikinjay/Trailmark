package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

/**
 * Fired when a player is credited with a kill. Keyed by the victim's entity type.
 */
public record KillSignal(ServerPlayer player, Entity victim, DamageSource source) implements Signal {
    @Override
    public Object key() {
        return victim.getType();
    }
}
