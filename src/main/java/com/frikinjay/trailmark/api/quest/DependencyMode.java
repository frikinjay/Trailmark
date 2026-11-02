package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;

/**
 * How many of a quest's dependencies have to be done before it unlocks.
 */
public enum DependencyMode {
    /** Every dependency. */
    ALL,
    /** Any one of them. */
    ANY,
    /** At least {@code minDependencies} of them. */
    AT_LEAST;

    public static final Codec<DependencyMode> CODEC = TrailmarkCodecs.enumCodec(DependencyMode.class);
}
