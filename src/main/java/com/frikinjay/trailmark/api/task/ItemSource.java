package com.frikinjay.trailmark.api.task;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A place Trailmark looks for items when counting or taking them for item tasks. The player's inventory
 * is always one; Curios, Trinkets, backpacks and storage networks plug in through here.
 * <pre>{@code
 * TrailmarkApi.registerItemSource(new ItemSource() { ... });
 * }</pre>
 */
public interface ItemSource {

    ResourceLocation id();

    /**
     * Show every stack this source holds for the player. Don't modify or keep the stacks.
     */
    void forEach(ServerPlayer player, Consumer<ItemStack> visitor);

    /**
     * Take up to {@code max} items matching {@code filter} and return how many were taken. Sources that can't
     * remove items return 0, and Trailmark moves on to the next source.
     */
    long extract(ServerPlayer player, Predicate<ItemStack> filter, long max);

    /**
     * Lower runs first. The player's inventory is 0.
     */
    default int priority() {
        return 100;
    }

    /**
     * Whether this source counts for detect-only tasks only, without ever giving items up. Storage networks the
     * player isn't standing next to are a good fit.
     */
    default boolean readOnly() {
        return false;
    }
}
