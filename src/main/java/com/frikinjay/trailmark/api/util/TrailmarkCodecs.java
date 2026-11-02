package com.frikinjay.trailmark.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Locale;

/**
 * Codecs that keep quest files pleasant to write by hand. Use them in your own task and reward types so
 * your fields accept the same shorthands as the built in ones.
 */
public final class TrailmarkCodecs {

    private TrailmarkCodecs() {}

    /**
     * Text that may be written as one string or as a list of lines. Lists are joined with newlines and
     * encoded back as a list when the text spans more than one line.
     */
    public static final Codec<String> TEXT = Codec.either(Codec.STRING, Codec.STRING.listOf()).xmap(
            either -> either.map(s -> s, lines -> String.join("\n", lines)),
            text -> text.indexOf('\n') < 0 ? Either.left(text) : Either.right(List.of(text.split("\n", -1))));

    /**
     * An item written either as {@code "minecraft:apple"} or as a full item stack object with
     * {@code id}, {@code count} and {@code components}.
     */
    public static final Codec<ItemStack> ITEM = Codec.either(BuiltInRegistries.ITEM.byNameCodec(), ItemStack.CODEC).xmap(
            either -> either.map(Item::getDefaultInstance, s -> s),
            stack -> stack.getCount() == 1 && stack.getComponentsPatch().isEmpty()
                    ? Either.left(stack.getItem())
                    : Either.right(stack));

    /**
     * Like {@link #ITEM} but allows an empty stack, written as {@code "minecraft:air"} or left out.
     */
    public static final Codec<ItemStack> OPTIONAL_ITEM = Codec.either(BuiltInRegistries.ITEM.byNameCodec(), ItemStack.OPTIONAL_CODEC).xmap(
            either -> either.map(item -> item == Items.AIR ? ItemStack.EMPTY : item.getDefaultInstance(), s -> s),
            stack -> stack.isEmpty() || (stack.getCount() == 1 && stack.getComponentsPatch().isEmpty())
                    ? Either.left(stack.isEmpty() ? Items.AIR : stack.getItem())
                    : Either.right(stack));

    /**
     * A positive amount written as a plain number. Quest files often hold counts in the billions for
     * energy or fluid, so this is a long.
     */
    public static final Codec<Long> AMOUNT = Codec.LONG.validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "Amount must be above zero, got " + v));

    /**
     * Lower case enum codec that ignores case on read, so {@code "Daily"} and {@code "daily"} both work.
     */
    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type) {
        E[] values = type.getEnumConstants();
        return Codec.STRING.comapFlatMap(name -> {
            String wanted = name.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
            for (E value : values) {
                if (value.name().equals(wanted)) return DataResult.success(value);
            }
            return DataResult.error(() -> "Unknown " + type.getSimpleName() + " '" + name + "'");
        }, value -> value.name().toLowerCase(Locale.ROOT));
    }

    /**
     * A resource location where a bare path falls back to {@code defaultNamespace} instead of {@code minecraft}.
     */
    public static Codec<ResourceLocation> idIn(String defaultNamespace) {
        return Codec.STRING.comapFlatMap(text -> {
            ResourceLocation id = text.indexOf(':') >= 0 ? ResourceLocation.tryParse(text) : ResourceLocation.tryBuild(defaultNamespace, text);
            return id == null ? DataResult.error(() -> "Not a valid id: " + text) : DataResult.success(id);
        }, id -> id.getNamespace().equals(defaultNamespace) ? id.getPath() : id.toString());
    }

    /**
     * A list that may also be written as a single value.
     */
    public static <T> Codec<List<T>> singleOrList(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(
                either -> either.map(List::of, l -> l),
                list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));
    }
}
