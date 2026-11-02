package com.frikinjay.trailmark.api.task;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Says which item stacks count for a task. Written in files as:
 * <ul>
 *     <li>{@code "minecraft:iron_ingot"} for one item,</li>
 *     <li>{@code "#c:ingots/iron"} for an item tag,</li>
 *     <li>{@code ["minecraft:oak_log", "#minecraft:logs"]} for any of several, or</li>
 *     <li>{@code {"items": [...], "components": {...}}} when the stack has to carry certain data components.</li>
 * </ul>
 * Component matching is partial: the stack has to have the listed components with those values, anything
 * else on it is ignored.
 *
 * @param items      exact items, any of which match
 * @param tags       item tags, any of which match
 * @param components components the stack must carry
 */
public record ItemMatcher(List<Holder<Item>> items, List<TagKey<Item>> tags, DataComponentPredicate components) implements Predicate<ItemStack> {

    public static final ItemMatcher NONE = new ItemMatcher(List.of(), List.of(), DataComponentPredicate.EMPTY);

    private static final Codec<Either<TagKey<Item>, Holder<Item>>> ENTRY = Codec.STRING.comapFlatMap(text -> {
        if (text.startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(text.substring(1));
            return id == null ? DataResult.error(() -> "Bad tag " + text) : DataResult.success(Either.left(TagKey.create(Registries.ITEM, id)));
        }
        ResourceLocation id = ResourceLocation.tryParse(text);
        if (id == null) return DataResult.error(() -> "Bad item id " + text);
        return BuiltInRegistries.ITEM.getHolder(id)
                .<DataResult<Either<TagKey<Item>, Holder<Item>>>>map(h -> DataResult.success(Either.right(h)))
                .orElseGet(() -> DataResult.error(() -> "Unknown item " + text));
    }, either -> either.map(tag -> "#" + tag.location(), holder -> holder.unwrapKey().map(k -> k.location().toString()).orElse("minecraft:air")));

    private static final Codec<List<Either<TagKey<Item>, Holder<Item>>>> ENTRIES = Codec.either(ENTRY, ENTRY.listOf()).xmap(
            e -> e.map(List::of, l -> l), l -> l.size() == 1 ? Either.left(l.get(0)) : Either.right(l));

    private static final Codec<ItemMatcher> FULL = RecordCodecBuilder.create(i -> i.group(
            ENTRIES.optionalFieldOf("items", List.of()).forGetter(ItemMatcher::entries),
            DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(ItemMatcher::components)
    ).apply(i, (entries, components) -> fromEntries(entries, components)));

    public static final Codec<ItemMatcher> CODEC = Codec.either(ENTRIES, FULL).xmap(
            either -> either.map(entries -> fromEntries(entries, DataComponentPredicate.EMPTY), m -> m),
            m -> m.components.equals(DataComponentPredicate.EMPTY) ? Either.left(m.entries()) : Either.right(m));

    public static ItemMatcher of(Item item) {
        return new ItemMatcher(List.of(BuiltInRegistries.ITEM.wrapAsHolder(item)), List.of(), DataComponentPredicate.EMPTY);
    }

    public static ItemMatcher of(TagKey<Item> tag) {
        return new ItemMatcher(List.of(), List.of(tag), DataComponentPredicate.EMPTY);
    }

    private static ItemMatcher fromEntries(List<Either<TagKey<Item>, Holder<Item>>> entries, DataComponentPredicate components) {
        List<Holder<Item>> items = new ArrayList<>();
        List<TagKey<Item>> tags = new ArrayList<>();
        for (var e : entries) e.ifLeft(tags::add).ifRight(items::add);
        return new ItemMatcher(List.copyOf(items), List.copyOf(tags), components);
    }

    private List<Either<TagKey<Item>, Holder<Item>>> entries() {
        List<Either<TagKey<Item>, Holder<Item>>> out = new ArrayList<>();
        for (TagKey<Item> tag : tags) out.add(Either.left(tag));
        for (Holder<Item> item : items) out.add(Either.right(item));
        return out;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) return false;
        boolean match = false;
        for (Holder<Item> item : items) {
            if (stack.is(item)) {
                match = true;
                break;
            }
        }
        if (!match) {
            for (TagKey<Item> tag : tags) {
                if (stack.is(tag)) {
                    match = true;
                    break;
                }
            }
        }
        return match && components.test(stack);
    }

    /**
     * Whether this matches exactly one item with no tags, which lets the dispatcher key it.
     */
    public Optional<Item> singleItem() {
        return items.size() == 1 && tags.isEmpty() ? Optional.of(items.get(0).value()) : Optional.empty();
    }

    /**
     * Every item this could match, for showing a cycling icon. Tags are resolved against the current registry.
     */
    public List<ItemStack> displayStacks() {
        List<ItemStack> out = new ArrayList<>();
        for (Holder<Item> item : items) out.add(new ItemStack(item));
        for (TagKey<Item> tag : tags) {
            BuiltInRegistries.ITEM.getTag(tag).map(HolderSet.Named::stream).ifPresent(s -> s.forEach(h -> out.add(new ItemStack(h))));
        }
        return out;
    }

    public boolean isEmpty() {
        return items.isEmpty() && tags.isEmpty();
    }
}
