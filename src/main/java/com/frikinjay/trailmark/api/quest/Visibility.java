package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;

/**
 * When players get to see a quest or chapter.
 */
public enum Visibility {
    /** Always shown, locked or not. */
    VISIBLE,
    /** Shown once at least one dependency is visible, so the map reveals itself as players progress. */
    REVEAL_WITH_DEPENDENCIES,
    /** Hidden until the player can start it. */
    HIDDEN_UNTIL_UNLOCKED,
    /** Hidden until it is completed. Good for surprise achievements. */
    SECRET;

    public static final Codec<Visibility> CODEC = TrailmarkCodecs.enumCodec(Visibility.class);
}
