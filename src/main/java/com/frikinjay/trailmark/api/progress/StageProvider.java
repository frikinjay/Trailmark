package com.frikinjay.trailmark.api.progress;

import net.minecraft.server.level.ServerPlayer;

/**
 * Lets another stage system answer "does this player have stage X". Trailmark's own team stages are always
 * checked first; providers are asked after that.
 */
@FunctionalInterface
public interface StageProvider {

    boolean hasStage(ServerPlayer player, String stage);

    /**
     * Grant or take a stage in the other system. Return false if it doesn't support writing.
     */
    default boolean setStage(ServerPlayer player, String stage, boolean present) {
        return false;
    }
}
