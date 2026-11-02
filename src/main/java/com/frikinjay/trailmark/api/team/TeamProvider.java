package com.frikinjay.trailmark.api.team;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.UUID;

/**
 * Decides who shares quest progress with whom. Trailmark ships a built-in party system and switches to
 * FTB Teams automatically when it's installed; register your own to hook up another team mod.
 * <p>
 * The active provider is the highest {@link #priority()} one that's {@link #isAvailable() available}.
 * Team ids must be stable across restarts because progress is saved against them.
 */
public interface TeamProvider {

    String id();

    int priority();

    boolean isAvailable();

    /**
     * The team of a player, online or not. Players without a team get a solo team, conventionally their own UUID.
     */
    UUID teamOf(MinecraftServer server, UUID player);

    Collection<UUID> members(MinecraftServer server, UUID team);

    Component teamName(MinecraftServer server, UUID team);
}
