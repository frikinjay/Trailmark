package com.frikinjay.trailmark.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Matches registry entries by id or by tag, written like {@code "minecraft:zombie"}, {@code "#minecraft:raiders"}
 * or a list of those. Works for any registry, including dynamic ones such as biomes and structures.
 *
 * @param registry the registry it reads from
 * @param ids      exact ids
 * @param tags     tags
 */
public record RegistryMatcher<T>(ResourceKey<? extends Registry<T>> registry, List<ResourceLocation> ids, List<TagKey<T>> tags) {

    public static <T> Codec<RegistryMatcher<T>> codec(ResourceKey<? extends Registry<T>> registry) {
        Codec<Either<TagKey<T>, ResourceLocation>> entry = Codec.STRING.comapFlatMap(text -> {
            boolean tag = text.startsWith("#");
            ResourceLocation id = ResourceLocation.tryParse(tag ? text.substring(1) : text);
            if (id == null) return DataResult.error(() -> "Not a valid id: " + text);
            return DataResult.success(tag ? Either.left(TagKey.create(registry, id)) : Either.right(id));
        }, e -> e.map(t -> "#" + t.location(), ResourceLocation::toString));
        return TrailmarkCodecs.singleOrList(entry).xmap(list -> {
            List<ResourceLocation> ids = new ArrayList<>();
            List<TagKey<T>> tags = new ArrayList<>();
            for (var e : list) e.ifLeft(tags::add).ifRight(ids::add);
            return new RegistryMatcher<>(registry, List.copyOf(ids), List.copyOf(tags));
        }, m -> {
            List<Either<TagKey<T>, ResourceLocation>> out = new ArrayList<>();
            for (TagKey<T> t : m.tags) out.add(Either.left(t));
            for (ResourceLocation id : m.ids) out.add(Either.right(id));
            return out;
        });
    }

    public static <T> RegistryMatcher<T> of(ResourceKey<? extends Registry<T>> registry, ResourceLocation id) {
        return new RegistryMatcher<>(registry, List.of(id), List.of());
    }

    public static <T> RegistryMatcher<T> any(ResourceKey<? extends Registry<T>> registry) {
        return new RegistryMatcher<>(registry, List.of(), List.of());
    }

    /**
     * True when this matches everything, which is what an empty matcher means.
     */
    public boolean isAny() {
        return ids.isEmpty() && tags.isEmpty();
    }

    public boolean test(Holder<T> holder) {
        if (isAny()) return true;
        Optional<ResourceKey<T>> key = holder.unwrapKey();
        if (key.isPresent() && ids.contains(key.get().location())) return true;
        for (TagKey<T> tag : tags) {
            if (holder.is(tag)) return true;
        }
        return false;
    }

    public boolean test(ResourceLocation id, Holder<T> holder) {
        if (isAny() || ids.contains(id)) return true;
        for (TagKey<T> tag : tags) {
            if (holder.is(tag)) return true;
        }
        return false;
    }

    /**
     * The single id when this matches exactly one entry and no tags, so dispatch can key on it.
     */
    public Optional<ResourceLocation> single() {
        return ids.size() == 1 && tags.isEmpty() ? Optional.of(ids.get(0)) : Optional.empty();
    }

    /**
     * A readable name: the id, the tag, or the first entry with "and more".
     */
    public String describe() {
        if (isAny()) return "*";
        String first = !ids.isEmpty() ? ids.get(0).toString() : "#" + tags.get(0).location();
        int more = ids.size() + tags.size() - 1;
        return more > 0 ? first + " +" + more : first;
    }
}
