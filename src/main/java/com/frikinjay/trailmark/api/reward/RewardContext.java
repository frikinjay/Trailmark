package com.frikinjay.trailmark.api.reward;

import com.frikinjay.trailmark.api.quest.Quest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.UUID;

/**
 * What a reward gets when it's handed out.
 *
 * @param player the player claiming it. For team rewards this is whoever pressed claim
 * @param teamId their team
 * @param quest  the quest it belongs to
 * @param choice the option the player picked, for rewards with {@link Reward#choices()}; -1 otherwise
 * @param random a random source to roll with
 */
public record RewardContext(MinecraftServer server, ServerPlayer player, UUID teamId, Quest quest, int choice, RandomSource random) {
}
