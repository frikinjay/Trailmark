package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;

/**
 * A periodic nudge for tasks that have to look at the world themselves, like "stand in this biome".
 * It's only sent to players whose team has such a task active, at the poll interval set in the config.
 */
public record TickSignal(ServerPlayer player) implements Signal {
}
