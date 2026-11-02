package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * A picture, video, label or rich text block placed on a chapter map. Decorations never affect progress.
 *
 * @param x            centre x in grid units, like quests
 * @param y            centre y in grid units
 * @param width        width in grid units
 * @param height       height in grid units
 * @param rotation     degrees clockwise
 * @param image        a texture, media file, GIF, video or link
 * @param text         markdown shown on top of (or instead of) the image
 * @param layer        older packs' way of stacking: {@code "back"} (the default) draws under the quests, {@code "front"}
 *                     above them; only used when {@code z} is not set
 * @param opacity      0 to 1
 * @param link         a link opened on click: a web address, {@code "quest:<id>"} or {@code "chapter:<id>"}
 * @param visibleAfter only show it once this quest is complete
 * @param style        style classes for the stylesheet
 * @param icon         an icon drawn to fill the decoration: a block, a structure, an animated mob, an item...
 * @param anchor       a quest to stay attached to; {@code x} and {@code y} are then offsets from that quest's centre
 * @param audio        a sound to play on click or on its own, and whether a video plays its own sound
 * @param z            stacking order on the map: higher draws on top of lower, across quests and decorations alike
 */
public record Decoration(float x, float y, float width, float height, float rotation, Optional<String> image, String text,
                         String layer, float opacity, Optional<String> link, Optional<String> visibleAfter, String style,
                         Optional<Icon> icon, Optional<String> anchor, Audio audio, Optional<Integer> z) {

    /**
     * Sound for a decoration.
     *
     * @param sound      a sound event ({@code minecraft:music_disc.cat}) or an OGG file ({@code media/theme.ogg}, a link
     *                   or a resource pack path); played when the decoration is clicked
     * @param volume     0 to 1, on top of the player's Jukebox/Note Blocks volume
     * @param loop       keep playing until clicked again
     * @param autoplay   start when the chapter opens, for background music or ambience
     * @param videoSound when the picture is a video, play the video's own sound with it
     * @param unlockAfter a quest that must be completed before the sound or video can be played; until then the
     *                    decoration shows a lock
     */
    public record Audio(Optional<String> sound, float volume, boolean loop, boolean autoplay, boolean videoSound, Optional<String> unlockAfter) {
        public static final Audio NONE = new Audio(Optional.empty(), 1.0F, false, false, false, Optional.empty());

        public Audio(Optional<String> sound, float volume, boolean loop, boolean autoplay, boolean videoSound) {
            this(sound, volume, loop, autoplay, videoSound, Optional.empty());
        }

        public static final MapCodec<Audio> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.optionalFieldOf("sound").forGetter(Audio::sound),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("volume", 1.0F).forGetter(Audio::volume),
                Codec.BOOL.optionalFieldOf("loop", false).forGetter(Audio::loop),
                Codec.BOOL.optionalFieldOf("autoplay", false).forGetter(Audio::autoplay),
                Codec.BOOL.optionalFieldOf("video_sound", false).forGetter(Audio::videoSound),
                Codec.STRING.optionalFieldOf("unlock_after").forGetter(Audio::unlockAfter)
        ).apply(i, Audio::new));
    }

    public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter(Decoration::x),
            Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter(Decoration::y),
            Codec.FLOAT.optionalFieldOf("width", 1.0F).forGetter(Decoration::width),
            Codec.FLOAT.optionalFieldOf("height", 1.0F).forGetter(Decoration::height),
            Codec.FLOAT.optionalFieldOf("rotation", 0.0F).forGetter(Decoration::rotation),
            Codec.STRING.optionalFieldOf("image").forGetter(Decoration::image),
            TrailmarkCodecs.TEXT.optionalFieldOf("text", "").forGetter(Decoration::text),
            Codec.STRING.optionalFieldOf("layer", "back").forGetter(Decoration::layer),
            Codec.FLOAT.optionalFieldOf("opacity", 1.0F).forGetter(Decoration::opacity),
            Codec.STRING.optionalFieldOf("link").forGetter(Decoration::link),
            Codec.STRING.optionalFieldOf("visible_after").forGetter(Decoration::visibleAfter),
            Codec.STRING.optionalFieldOf("style", "").forGetter(Decoration::style),
            Icon.CODEC.optionalFieldOf("icon").forGetter(Decoration::icon),
            Codec.STRING.optionalFieldOf("anchor").forGetter(Decoration::anchor),
            Audio.MAP_CODEC.forGetter(Decoration::audio),
            Codec.INT.optionalFieldOf("z").forGetter(Decoration::z)
    ).apply(i, Decoration::new));

    public Decoration(float x, float y, float width, float height, float rotation, Optional<String> image, String text, String layer, float opacity,
                      Optional<String> link, Optional<String> visibleAfter, String style, Optional<Icon> icon, Optional<String> anchor, Audio audio) {
        this(x, y, width, height, rotation, image, text, layer, opacity, link, visibleAfter, style, icon, anchor, audio, Optional.empty());
    }

    public Decoration(float x, float y, float width, float height, float rotation, Optional<String> image, String text, String layer, float opacity,
                      Optional<String> link, Optional<String> visibleAfter, String style) {
        this(x, y, width, height, rotation, image, text, layer, opacity, link, visibleAfter, style, Optional.empty(), Optional.empty(), Audio.NONE, Optional.empty());
    }

    public Decoration(float x, float y, float width, float height, float rotation, Optional<String> image, String text, String layer, float opacity,
                      Optional<String> link, Optional<String> visibleAfter, String style, Optional<Icon> icon, Optional<String> anchor) {
        this(x, y, width, height, rotation, image, text, layer, opacity, link, visibleAfter, style, icon, anchor, Audio.NONE, Optional.empty());
    }

    /**
     * This decoration at another place and size, keeping everything else.
     */
    public Decoration placed(float nx, float ny, float nw, float nh) {
        return new Decoration(nx, ny, nw, nh, rotation, image, text, layer, opacity, link, visibleAfter, style, icon, anchor, audio, z);
    }

    /**
     * This decoration with another stacking order.
     */
    public Decoration withZ(Optional<Integer> nz) {
        return new Decoration(x, y, width, height, rotation, image, text, layer, opacity, link, visibleAfter, style, icon, anchor, audio, nz);
    }

    /**
     * Where it stacks: its {@code z}, or for older packs -1 for {@code back} and 1 for {@code front} (quests are 0).
     */
    public int order() {
        return z.orElse(inFront() ? 1 : -1);
    }

    public boolean inFront() {
        return "front".equalsIgnoreCase(layer);
    }
}
