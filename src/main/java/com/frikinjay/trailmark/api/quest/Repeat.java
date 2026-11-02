package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Whether a quest can be done again after it's finished and all its rewards are claimed.
 * <p>
 * In files you can write {@code "repeat": "daily"} or {@code "repeat": {"mode": "cooldown", "cooldown": 600}}.
 * Daily and weekly resets happen at midnight UTC so every player on a server shares the same reset time.
 *
 * @param mode            how the quest resets
 * @param cooldownSeconds seconds to wait after claiming, used by {@link Mode#COOLDOWN}
 * @param maxTimes        how many times it can be completed in total, 0 meaning no limit
 */
public record Repeat(Mode mode, long cooldownSeconds, int maxTimes) {

    public static final Repeat NEVER = new Repeat(Mode.NEVER, 0, 0);

    private static final Codec<Repeat> FULL = RecordCodecBuilder.create(i -> i.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.NEVER).forGetter(Repeat::mode),
            Codec.LONG.optionalFieldOf("cooldown", 0L).forGetter(Repeat::cooldownSeconds),
            Codec.INT.optionalFieldOf("max_times", 0).forGetter(Repeat::maxTimes)
    ).apply(i, Repeat::new));

    public static final Codec<Repeat> CODEC = Codec.either(Mode.CODEC, FULL).xmap(
            either -> either.map(mode -> new Repeat(mode, 0, 0), r -> r),
            r -> r.cooldownSeconds == 0 && r.maxTimes == 0 ? Either.left(r.mode) : Either.right(r));

    public boolean repeats() {
        return mode != Mode.NEVER;
    }

    public enum Mode {
        NEVER,
        INSTANT,
        COOLDOWN,
        DAILY,
        WEEKLY;

        public static final Codec<Mode> CODEC = TrailmarkCodecs.enumCodec(Mode.class);
    }
}
