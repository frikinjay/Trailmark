package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * What a quest, chapter, task, reward or decoration shows as its picture: an item, a picture, a block model, a
 * structure or a live entity. When several are set the screen prefers structure, then entity, then block, then
 * picture, then item.
 * <p>
 * In files an icon is usually just a string: an item id ({@code "minecraft:oak_log"}), a picture when it looks like
 * a path or link ({@code "media/banner.gif"}), or one of {@code "block:minecraft:furnace[lit=true]"},
 * {@code "entity:minecraft:zombie"}, {@code "player:Notch"} (or {@code "player:@self"}),
 * {@code "structure:minecraft:igloo/top"}, {@code "fluid:minecraft:lava"} and {@code "sound:media/theme.ogg"}.
 *
 * @param item       the item to draw, or empty
 * @param image      a texture id, a file in the quest pack's media folder, or a link
 * @param entity     an entity type to render in 3D
 * @param entityData extra NBT for that entity, so you can show a saddled pig or a named villager
 * @param scale      size multiplier, 1 being normal
 * @param block      a block with optional state, in command syntax: {@code minecraft:furnace[lit=true,facing=south]}
 * @param structure  a structure template id, read from the server's data packs or a resource pack
 * @param view       camera and animation settings for blocks, structures and entities
 * @param figure     a player model, and equipment and a pose for players and other mobs
 * @param fluid      a fluid, drawn with its own texture and colour; works for modded fluids that have no bucket
 * @param sound      a sound to play from the icon (a sound ID, a sound file or a link); a quest's sound plays from its
 *                   details panel once the quest is unlocked
 */
public record Icon(ItemStack item, Optional<String> image, Optional<ResourceLocation> entity, Optional<CompoundTag> entityData, float scale,
                   Optional<String> block, Optional<ResourceLocation> structure, View view, Figure figure, Optional<ResourceLocation> fluid,
                   Optional<String> sound) {

    public static final Icon EMPTY = new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F,
            Optional.empty(), Optional.empty(), View.DEFAULT, Figure.NONE, Optional.empty(), Optional.empty());

    public Icon(ItemStack item, Optional<String> image, Optional<ResourceLocation> entity, Optional<CompoundTag> entityData, float scale,
                Optional<String> block, Optional<ResourceLocation> structure, View view, Figure figure) {
        this(item, image, entity, entityData, scale, block, structure, view, figure, Optional.empty(), Optional.empty());
    }

    public Icon(ItemStack item, Optional<String> image, Optional<ResourceLocation> entity, Optional<CompoundTag> entityData, float scale,
                Optional<String> block, Optional<ResourceLocation> structure, View view, Figure figure, Optional<ResourceLocation> fluid) {
        this(item, image, entity, entityData, scale, block, structure, view, figure, fluid, Optional.empty());
    }

    /**
     * A player model, or extras for any mob.
     *
     * @param player    whose skin to wear: a username, a UUID, or {@code @self} for whoever is looking at the screen
     * @param skin      a skin texture instead, such as {@code mypack:textures/skins/guide.png}
     * @param slim      slim (Alex) arms; empty means whatever the skin says
     * @param equipment items by slot: {@code head}, {@code chest}, {@code legs}, {@code feet}, {@code mainhand},
     *                  {@code offhand}
     * @param pose      rotations in degrees ({@code [x, y, z]}) by part: {@code head}, {@code body}, {@code right_arm},
     *                  {@code left_arm}, {@code right_leg}, {@code left_leg}. Works on players and every mob with a
     *                  humanoid model.
     */
    public record Figure(Optional<String> player, Optional<String> skin, Optional<Boolean> slim, Map<String, ItemStack> equipment,
                         Map<String, List<Float>> pose) {

        public static final Figure NONE = new Figure(Optional.empty(), Optional.empty(), Optional.empty(), Map.of(), Map.of());

        public static final MapCodec<Figure> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.optionalFieldOf("player").forGetter(Figure::player),
                Codec.STRING.optionalFieldOf("skin").forGetter(Figure::skin),
                Codec.BOOL.optionalFieldOf("slim").forGetter(Figure::slim),
                Codec.unboundedMap(Codec.STRING, TrailmarkCodecs.OPTIONAL_ITEM).optionalFieldOf("equipment", Map.of()).forGetter(Figure::equipment),
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT.listOf()).optionalFieldOf("pose", Map.of()).forGetter(Figure::pose)
        ).apply(i, Figure::new));

        public boolean isPlayer() {
            return player.isPresent() || skin.isPresent();
        }

        public boolean isEmpty() {
            return equals(NONE);
        }
    }

    /**
     * How a 3D icon is shown.
     *
     * @param yaw       turn in degrees, or empty for the default three-quarter view
     * @param pitch     tilt in degrees, or empty for the default
     * @param spin      degrees per second it turns on its own; negative means the default (entities and structures turn,
     *                  single blocks don't)
     * @param animation for entities: {@code live} (breathing, idle animations), {@code walk} (also moves its legs) or
     *                  {@code frozen} (stopped at {@code time})
     * @param time      for {@code frozen}, the moment in ticks to freeze at
     * @param play      an animation to play: a vanilla one by name ({@code roar}, {@code flap}) or a GeckoLib one
     *                  ({@code walk}, or {@code controller/animation})
     * @param shiftX    moves the model sideways inside its icon, in icon widths (0.25 is a quarter to the right)
     * @param shiftY    moves the model up or down inside its icon, in icon heights (positive is down)
     */
    public record View(Optional<Float> yaw, Optional<Float> pitch, float spin, String animation, float time, Optional<String> play,
                       float shiftX, float shiftY, float speed) {

        public static final View DEFAULT = new View(Optional.empty(), Optional.empty(), -1, "live", 0, Optional.empty(), 0, 0, 1);

        public View(Optional<Float> yaw, Optional<Float> pitch, float spin, String animation, float time, Optional<String> play) {
            this(yaw, pitch, spin, animation, time, play, 0, 0, 1);
        }

        public View(Optional<Float> yaw, Optional<Float> pitch, float spin, String animation, float time, Optional<String> play,
                    float shiftX, float shiftY) {
            this(yaw, pitch, spin, animation, time, play, shiftX, shiftY, 1);
        }

        public static final MapCodec<View> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.FLOAT.optionalFieldOf("yaw").forGetter(View::yaw),
                Codec.FLOAT.optionalFieldOf("pitch").forGetter(View::pitch),
                Codec.FLOAT.optionalFieldOf("spin", -1F).forGetter(View::spin),
                Codec.STRING.optionalFieldOf("animation", "live").forGetter(View::animation),
                Codec.FLOAT.optionalFieldOf("time", 0F).forGetter(View::time),
                Codec.STRING.optionalFieldOf("play").forGetter(View::play),
                Codec.floatRange(-4, 4).optionalFieldOf("shift_x", 0F).forGetter(View::shiftX),
                Codec.floatRange(-4, 4).optionalFieldOf("shift_y", 0F).forGetter(View::shiftY),
                Codec.floatRange(0, 10).optionalFieldOf("animation_speed", 1F).forGetter(View::speed)
        ).apply(i, View::new));

        public boolean frozen() {
            return animation.toLowerCase(Locale.ROOT).equals("frozen");
        }

        public boolean walking() {
            return animation.toLowerCase(Locale.ROOT).equals("walk");
        }
    }

    private static final Codec<Icon> FULL = RecordCodecBuilder.create(i -> i.group(
            TrailmarkCodecs.OPTIONAL_ITEM.optionalFieldOf("item", ItemStack.EMPTY).forGetter(Icon::item),
            Codec.STRING.optionalFieldOf("image").forGetter(Icon::image),
            ResourceLocation.CODEC.optionalFieldOf("entity").forGetter(Icon::entity),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(Icon::entityData),
            Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Icon::scale),
            Codec.STRING.optionalFieldOf("block").forGetter(Icon::block),
            ResourceLocation.CODEC.optionalFieldOf("structure").forGetter(Icon::structure),
            View.MAP_CODEC.forGetter(Icon::view),
            Figure.MAP_CODEC.forGetter(Icon::figure),
            ResourceLocation.CODEC.optionalFieldOf("fluid").forGetter(Icon::fluid),
            Codec.STRING.optionalFieldOf("sound").forGetter(Icon::sound)
    ).apply(i, Icon::new));

    public static final Codec<Icon> CODEC = Codec.either(Codec.STRING, FULL).xmap(
            either -> either.map(Icon::parse, icon -> icon),
            icon -> icon.shorthand() != null ? Either.left(icon.shorthand()) : Either.right(icon));

    public Icon(ItemStack item, Optional<String> image, Optional<ResourceLocation> entity, Optional<CompoundTag> entityData, float scale) {
        this(item, image, entity, entityData, scale, Optional.empty(), Optional.empty(), View.DEFAULT, Figure.NONE);
    }

    public static Icon of(ItemStack stack) {
        return new Icon(stack.copyWithCount(1), Optional.empty(), Optional.empty(), Optional.empty(), 1.0F);
    }

    public static Icon image(String image) {
        return new Icon(ItemStack.EMPTY, Optional.of(image), Optional.empty(), Optional.empty(), 1.0F);
    }

    public static Icon entity(ResourceLocation type) {
        return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.of(type), Optional.empty(), 1.0F);
    }

    public static Icon block(String state) {
        return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F, Optional.of(state), Optional.empty(), View.DEFAULT, Figure.NONE);
    }

    /**
     * This icon shown another way (camera, animation, shift).
     */
    public Icon withView(View newView) {
        return new Icon(item, image, entity, entityData, scale, block, structure, newView, figure, fluid, sound);
    }

    /**
     * This icon at another size within its slot.
     */
    public Icon withScale(float newScale) {
        return new Icon(item, image, entity, entityData, newScale, block, structure, view, figure, fluid, sound);
    }

    public static Icon structure(ResourceLocation id) {
        return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F, Optional.empty(), Optional.of(id), View.DEFAULT, Figure.NONE);
    }

    public static Icon fluid(ResourceLocation id) {
        return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F, Optional.empty(), Optional.empty(), View.DEFAULT,
                Figure.NONE, Optional.of(id), Optional.empty());
    }

    public static Icon sound(String ref) {
        return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F, Optional.empty(), Optional.empty(), View.DEFAULT,
                Figure.NONE, Optional.empty(), Optional.of(ref));
    }

    public boolean isEmpty() {
        return item.isEmpty() && image.isEmpty() && entity.isEmpty() && block.isEmpty() && structure.isEmpty() && fluid.isEmpty() && sound.isEmpty() && !figure.isPlayer();
    }

    /**
     * Returns this icon, or {@code fallback} when this one is empty.
     */
    public Icon or(Icon fallback) {
        return isEmpty() ? fallback : this;
    }

    private String shorthand() {
        if (scale != 1.0F || entityData.isPresent() || !view.equals(View.DEFAULT)) return null;
        if (!figure.isEmpty()) {
            boolean plain = figure.player().isPresent() && figure.skin().isEmpty() && figure.slim().isEmpty() && figure.equipment().isEmpty()
                    && figure.pose().isEmpty() && item.isEmpty() && image.isEmpty() && entity.isEmpty() && block.isEmpty() && structure.isEmpty()
                    && fluid.isEmpty() && sound.isEmpty();
            return plain ? "player:" + figure.player().get() : null;
        }
        int kinds = (item.isEmpty() ? 0 : 1) + (image.isPresent() ? 1 : 0) + (entity.isPresent() ? 1 : 0) + (block.isPresent() ? 1 : 0)
                + (structure.isPresent() ? 1 : 0) + (fluid.isPresent() ? 1 : 0) + (sound.isPresent() ? 1 : 0);
        if (kinds != 1) return null;
        if (sound.isPresent()) return "sound:" + sound.get();
        if (fluid.isPresent()) return "fluid:" + fluid.get();
        if (entity.isPresent()) return "entity:" + entity.get();
        if (block.isPresent()) return "block:" + block.get();
        if (structure.isPresent()) return "structure:" + structure.get();
        if (image.isPresent()) return image.get();
        return item.getCount() == 1 && item.getComponentsPatch().isEmpty()
                ? net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem()).toString() : null;
    }

    private static Icon parse(String text) {
        String t = text.trim();
        if (t.isEmpty()) return EMPTY;
        String lower = t.toLowerCase(Locale.ROOT);
        if (lower.startsWith("block:")) return block(t.substring(6).trim());
        if (lower.startsWith("entity:")) {
            ResourceLocation id = ResourceLocation.tryParse(t.substring(7).trim());
            return id == null ? EMPTY : entity(id);
        }
        if (lower.startsWith("player:")) {
            String who = t.substring(7).trim();
            return new Icon(ItemStack.EMPTY, Optional.empty(), Optional.empty(), Optional.empty(), 1.0F, Optional.empty(), Optional.empty(), View.DEFAULT,
                    new Figure(Optional.of(who.isEmpty() ? "@self" : who), Optional.empty(), Optional.empty(), Map.of(), Map.of()));
        }
        if (lower.startsWith("sound:")) {
            String ref = t.substring(6).trim();
            return ref.isEmpty() ? EMPTY : sound(ref);
        }
        if (lower.startsWith("fluid:")) {
            ResourceLocation id = ResourceLocation.tryParse(t.substring(6).trim());
            return id == null ? EMPTY : fluid(id);
        }
        if (lower.startsWith("structure:")) {
            ResourceLocation id = ResourceLocation.tryParse(t.substring(10).trim());
            return id == null ? EMPTY : structure(id);
        }
        if (lower.startsWith("item:")) t = t.substring(5).trim();
        else if (looksLikeImage(t)) return image(t);
        ResourceLocation id = ResourceLocation.tryParse(t);
        if (id == null) return image(t);
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(id)
                .map(item -> of(item.getDefaultInstance()))
                .orElseGet(() -> image(text.trim()));
    }

    static boolean looksLikeImage(String t) {
        String lower = t.toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://") || lower.contains("/")
                || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg") || lower.endsWith(".webp") || lower.endsWith(".mp4") || lower.endsWith(".webm");
    }
}
