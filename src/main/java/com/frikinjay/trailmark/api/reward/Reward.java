package com.frikinjay.trailmark.api.reward;

import com.frikinjay.trailmark.api.quest.Icon;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Something a player gets for finishing a quest. Subclass it, give it a {@link RewardType}, and do the
 * actual giving in {@link #grant}. Rewards are shared, immutable definitions; never keep per-player state here.
 */
public abstract class Reward {

    private RewardInfo info = RewardInfo.DEFAULT;

    public abstract RewardType<?> type();

    /**
     * Hand the reward out. Return {@code false} if it couldn't be given right now (a full inventory for a
     * reward that refuses to drop items, say); the reward then stays claimable.
     */
    public abstract boolean grant(RewardContext context);

    public abstract Component defaultTitle();

    public Icon defaultIcon() {
        return type().icon();
    }

    /**
     * The options for pick-one rewards. When this isn't empty the screen asks the player to choose and
     * {@link RewardContext#choice()} holds their pick.
     */
    public List<Reward> choices() {
        return List.of();
    }

    /**
     * Extra lines for the reward's tooltip, such as the contents of a loot table.
     */
    /**
     * The item this reward gives, for its item tooltip and recipe lookups in the quest book; empty for rewards that
     * aren't one item.
     */
    public net.minecraft.world.item.ItemStack subjectItem() {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    public List<Component> tooltip() {
        return List.of();
    }

    public final RewardInfo info() {
        return info;
    }

    public final String id() {
        return info.id();
    }

    public final Component title() {
        return info.title().isEmpty() ? defaultTitle() : Component.literal(info.title());
    }

    public final Icon icon() {
        return info.icon().or(defaultIcon());
    }

    @ApiStatus.Internal
    public final void attachInfo(RewardInfo newInfo) {
        this.info = newInfo;
    }
}
