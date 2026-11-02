package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.TrailmarkRegistries;
import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One page of the quest book: a map of quests with its own title, icon, background and stylesheet.
 * The id comes from the file path, so {@code chapters/mining/deep.json} in the quest folder becomes
 * {@code pack:mining/deep}.
 */
public final class Chapter {

    private final ResourceLocation id;
    private final Def def;
    private final List<Quest> quests;
    private final List<ResourceLocation> requires;

    public Chapter(ResourceLocation id, Def def, List<Quest> quests) {
        this.id = id;
        this.def = def;
        this.quests = List.copyOf(quests);
        List<ResourceLocation> req = new ArrayList<>();
        for (String r : def.logic.requires) {
            if (!r.isBlank()) req.add(Quest.resolve(id, r));
        }
        this.requires = List.copyOf(req);
    }

    public ResourceLocation id() {
        return id;
    }

    public Def def() {
        return def;
    }

    public String title() {
        return def.basics.title;
    }

    public String subtitle() {
        return def.basics.subtitle;
    }

    public String description() {
        return def.basics.description;
    }

    public Icon icon() {
        return def.basics.icon;
    }

    public String group() {
        return def.basics.group;
    }

    public int order() {
        return def.basics.order;
    }

    public String styleClass() {
        return def.basics.style;
    }

    /**
     * A picture, GIF or video drawn behind the map.
     */
    public Optional<String> background() {
        return def.basics.background;
    }

    public Visibility visibility() {
        return def.logic.visibility;
    }

    /**
     * Quests or chapters that must be complete before this chapter opens. Chapter ids count as "every
     * non-optional quest in it is done".
     */
    public List<ResourceLocation> requires() {
        return requires;
    }

    public Visibility defaultQuestVisibility() {
        return def.logic.questVisibility;
    }

    /**
     * How dependency lines in this chapter are drawn, or empty to use the book's setting.
     */
    public String lineStyle() {
        return def.logic.lineStyle;
    }

    /**
     * When the chapter is open, if it's seasonal.
     */
    public Optional<Season> season() {
        return def.logic.season;
    }

    public NodeShape defaultShape() {
        return def.logic.defaultShape;
    }

    public List<Quest> quests() {
        return quests;
    }

    public List<Decoration> decorations() {
        return def.logic.decorations;
    }

    /**
     * Rewards for completing every non-optional quest in the chapter.
     */
    public List<Reward> rewards() {
        return def.logic.rewards;
    }

    @Nullable
    public Quest quest(String localId) {
        for (Quest q : quests) {
            if (q.localId().equals(localId)) return q;
        }
        return null;
    }

    public String titleKey() {
        return key("title");
    }

    public String subtitleKey() {
        return key("subtitle");
    }

    public String descriptionKey() {
        return key("description");
    }

    public String rewardTitleKey(Reward reward) {
        return key("reward." + reward.id() + ".title");
    }

    private String key(String suffix) {
        return "trailmark.chapter." + id.getNamespace() + "." + id.getPath().replace('/', '.') + "." + suffix;
    }

    @Override
    public String toString() {
        return "Chapter[" + id + "]";
    }

    /**
     * A chapter file exactly as written.
     */
    public record Def(Basics basics, Logic logic, List<Quest.Def> quests) {
        public static final Codec<Def> CODEC = Codec.mapPair(Basics.MAP_CODEC, Codec.mapPair(Logic.MAP_CODEC,
                        Quest.Def.CODEC.listOf().optionalFieldOf("quests", List.of())))
                .xmap(p -> new Def(p.getFirst(), p.getSecond().getFirst(), p.getSecond().getSecond()),
                        d -> Pair.of(d.basics, Pair.of(d.logic, d.quests)))
                .codec();

        public Def withQuests(List<Quest.Def> newQuests) {
            return new Def(basics, logic, newQuests);
        }

        public Def withBasics(Basics newBasics) {
            return new Def(newBasics, logic, quests);
        }

        public Def withLogic(Logic newLogic) {
            return new Def(basics, newLogic, quests);
        }
    }

    public record Basics(String title, String subtitle, String description, Icon icon, String group, int order, String style,
                         Optional<String> background) {
        public static final MapCodec<Basics> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.optionalFieldOf("title", "").forGetter(Basics::title),
                Codec.STRING.optionalFieldOf("subtitle", "").forGetter(Basics::subtitle),
                TrailmarkCodecs.TEXT.optionalFieldOf("description", "").forGetter(Basics::description),
                Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(Basics::icon),
                Codec.STRING.optionalFieldOf("group", "").forGetter(Basics::group),
                Codec.INT.optionalFieldOf("order", 0).forGetter(Basics::order),
                Codec.STRING.optionalFieldOf("style", "").forGetter(Basics::style),
                Codec.STRING.optionalFieldOf("background").forGetter(Basics::background)
        ).apply(i, Basics::new));
    }

    public record Logic(Visibility visibility, List<String> requires, Visibility questVisibility, NodeShape defaultShape,
                        List<Decoration> decorations, List<Reward> rewards, String lineStyle, Optional<Season> season) {
        public static final MapCodec<Logic> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Visibility.CODEC.optionalFieldOf("visibility", Visibility.VISIBLE).forGetter(Logic::visibility),
                TrailmarkCodecs.singleOrList(Codec.STRING).optionalFieldOf("requires", List.of()).forGetter(Logic::requires),
                Visibility.CODEC.optionalFieldOf("quest_visibility", Visibility.VISIBLE).forGetter(Logic::questVisibility),
                NodeShape.CODEC.optionalFieldOf("default_shape", NodeShape.ROUNDED).forGetter(Logic::defaultShape),
                Decoration.CODEC.listOf().optionalFieldOf("decorations", List.of()).forGetter(Logic::decorations),
                TrailmarkRegistries.REWARD_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Logic::rewards),
                Codec.STRING.optionalFieldOf("line_style", "").forGetter(Logic::lineStyle),
                Season.CODEC.optionalFieldOf("season").forGetter(Logic::season)
        ).apply(i, Logic::new));

        public Logic(Visibility visibility, List<String> requires, Visibility questVisibility, NodeShape defaultShape, List<Decoration> decorations,
                     List<Reward> rewards, String lineStyle) {
            this(visibility, requires, questVisibility, defaultShape, decorations, rewards, lineStyle, Optional.empty());
        }

        public Logic(Visibility visibility, List<String> requires, Visibility questVisibility, NodeShape defaultShape, List<Decoration> decorations,
                     List<Reward> rewards) {
            this(visibility, requires, questVisibility, defaultShape, decorations, rewards, "", Optional.empty());
        }
    }
}
