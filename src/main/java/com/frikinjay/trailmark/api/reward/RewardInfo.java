package com.frikinjay.trailmark.api.reward;

import com.frikinjay.trailmark.api.quest.Icon;
import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Fields every reward has. Like task info, they share the object with the type's own fields, so keep
 * these names free: {@code type}, {@code id}, {@code title}, {@code icon}, {@code claim}, {@code auto_claim},
 * {@code hidden}, {@code exclude_from_claim_all}.
 *
 * @param id                  unique within the quest, defaults to its position
 * @param title               custom title, or empty for the generated one
 * @param icon                custom icon, or empty for the generated one
 * @param claim               who gets it: each team member separately, or the team once. Empty uses the book default
 * @param autoClaim           hand it out the moment the quest completes. Empty uses the quest's setting
 * @param hidden              shown as a mystery reward until claimed
 * @param excludeFromClaimAll skipped by the "claim all" button, for rewards people should pick up on purpose
 */
public record RewardInfo(String id, String title, Icon icon, Optional<ClaimMode> claim, Optional<Boolean> autoClaim,
                         boolean hidden, boolean excludeFromClaimAll) {

    public static final RewardInfo DEFAULT = new RewardInfo("", "", Icon.EMPTY, Optional.empty(), Optional.empty(), false, false);

    public static final MapCodec<RewardInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("id", "").forGetter(RewardInfo::id),
            Codec.STRING.optionalFieldOf("title", "").forGetter(RewardInfo::title),
            Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(RewardInfo::icon),
            ClaimMode.CODEC.optionalFieldOf("claim").forGetter(RewardInfo::claim),
            Codec.BOOL.optionalFieldOf("auto_claim").forGetter(RewardInfo::autoClaim),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(RewardInfo::hidden),
            Codec.BOOL.optionalFieldOf("exclude_from_claim_all", false).forGetter(RewardInfo::excludeFromClaimAll)
    ).apply(i, RewardInfo::new));

    public RewardInfo withId(String newId) {
        return new RewardInfo(newId, title, icon, claim, autoClaim, hidden, excludeFromClaimAll);
    }

    /**
     * Who a reward is for.
     */
    public enum ClaimMode {
        /** Every team member claims their own copy. */
        PLAYER,
        /** The first member to claim takes it for the whole team. */
        TEAM;

        public static final Codec<ClaimMode> CODEC = TrailmarkCodecs.enumCodec(ClaimMode.class);
    }
}
