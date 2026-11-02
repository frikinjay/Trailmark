package com.frikinjay.trailmark.api.signal;

import net.minecraft.server.level.ServerPlayer;

/**
 * Fired a short, configurable moment after a player's inventory changes. Bursts of changes are merged, so
 * shift-clicking a stack out of a chest fires this once rather than sixty times.
 */
public record InventorySignal(ServerPlayer player) implements Signal {
}
