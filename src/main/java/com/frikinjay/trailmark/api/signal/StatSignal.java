package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;

/**
 * Fired whenever the game bumps a statistic for a player. Vanilla tracks a lot through stats: items crafted
 * (including smelted), blocks mined, items used (placing blocks, eating, drinking), fish caught, animals bred,
 * villager trades, enchantments and more. Keyed by the {@link Stat} itself.
 *
 * @param amount how much it went up by this time
 */
public record StatSignal(ServerPlayer player, Stat<?> stat, int amount) implements Signal {
    @Override
    public Object key() {
        return stat;
    }
}
