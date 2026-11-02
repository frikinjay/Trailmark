package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;

/**
 * A free-form signal with a string key and an amount. The {@code custom} and {@code counter} task types
 * listen for these, and the {@code /trailmark signal} command fires them, so datapacks, KubeJS scripts and
 * other mods can drive quests without writing a task type.
 */
public record CustomSignal(ServerPlayer player, String name, long amount) implements Signal {
    @Override
    public Object key() {
        return name;
    }
}
