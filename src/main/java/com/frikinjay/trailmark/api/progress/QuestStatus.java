package com.frikinjay.trailmark.api.progress;

import net.minecraft.util.StringRepresentable;

/**
 * Where a quest stands for one player. The first four follow each other in that order; the last two are
 * side states for repeatable and exclusive quests.
 */
public enum QuestStatus implements StringRepresentable {
    /** Dependencies aren't done yet. */
    LOCKED("locked"),
    /** Unlocked, nothing done yet. */
    AVAILABLE("available"),
    /** Some task progress. */
    IN_PROGRESS("in_progress"),
    /** All required tasks done, and this player still has rewards to claim. */
    CLAIMABLE("claimable"),
    /** Done and claimed. */
    COMPLETED("completed"),
    /** A repeatable quest waiting for its cooldown or reset. */
    COOLDOWN("cooldown"),
    /** Locked forever because the team picked another branch. */
    EXCLUDED("excluded");

    private final String name;

    QuestStatus(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isUnlocked() {
        return this == AVAILABLE || this == IN_PROGRESS || this == CLAIMABLE;
    }

    public boolean isDone() {
        return this == CLAIMABLE || this == COMPLETED || this == COOLDOWN;
    }
}
