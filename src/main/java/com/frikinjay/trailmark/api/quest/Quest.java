package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.TrailmarkRegistries;
import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.task.Task;
import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One quest on a chapter map. Quests are immutable: the editor and {@code /reload} build a fresh book
 * rather than changing these in place, so it's safe to hold on to one for the duration of a tick.
 * <p>
 * A quest's full id is {@code <chapter namespace>:<chapter path>/<local id>}, for example
 * {@code pack:getting_started/first_tree}. Progress is saved against that id, so renaming a quest's id
 * resets everyone's progress on it.
 */
public final class Quest {

    private final ResourceLocation id;
    private final ResourceLocation chapterId;
    private final Def def;
    private final List<ResourceLocation> dependencies;
    private final List<ResourceLocation> exclusiveWith;
    private int index = -1;

    @ApiStatus.Internal
    public Quest(ResourceLocation chapterId, Def def) {
        this.chapterId = chapterId;
        this.def = def;
        this.id = ResourceLocation.fromNamespaceAndPath(chapterId.getNamespace(), chapterId.getPath() + "/" + def.basics.id);
        this.dependencies = resolveAll(chapterId, def.logic.dependencies);
        this.exclusiveWith = resolveAll(chapterId, def.logic.exclusiveWith);
    }

    /**
     * Turns a reference written in a quest file into a full quest id. {@code "first_tree"} means a quest in
     * the same chapter; anything with a colon is taken as a full id.
     */
    public static ResourceLocation resolve(ResourceLocation chapterId, String ref) {
        String r = ref.trim();
        if (r.indexOf(':') >= 0) {
            ResourceLocation id = ResourceLocation.tryParse(r);
            if (id != null) return id;
        }
        return ResourceLocation.fromNamespaceAndPath(chapterId.getNamespace(), chapterId.getPath() + "/" + r);
    }

    private static List<ResourceLocation> resolveAll(ResourceLocation chapterId, List<String> refs) {
        List<ResourceLocation> out = new ArrayList<>(refs.size());
        for (String ref : refs) {
            if (!ref.isBlank()) out.add(resolve(chapterId, ref));
        }
        return List.copyOf(out);
    }

    public ResourceLocation id() {
        return id;
    }

    public String localId() {
        return def.basics.id;
    }

    public ResourceLocation chapterId() {
        return chapterId;
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

    public List<String> tags() {
        return def.basics.tags;
    }

    /**
     * Extra style classes for the quest screen stylesheet, separated by spaces.
     */
    public String styleClass() {
        return def.basics.style;
    }

    public String hint() {
        return def.basics.hint;
    }

    public float x() {
        return def.layout.x;
    }

    public float y() {
        return def.layout.y;
    }

    public float size() {
        return def.layout.size;
    }

    /**
     * Where the quest stacks among the chapter's quests and decorations (0 unless the pack set one).
     */
    public int order() {
        return def.layout.z.orElse(0);
    }

    public Optional<NodeShape> shape() {
        return def.layout.shape;
    }

    public boolean hideDependencyLines() {
        return def.layout.hideDependencyLines;
    }

    /**
     * The line style set on this quest for the line from {@code dependency} (as written in {@code dependencies},
     * or its full id), else for all its lines; empty means inherit.
     */
    public String lineStyle(ResourceLocation dependency) {
        Map<String, String> per = def.layout.lineStyles;
        if (!per.isEmpty()) {
            String full = dependency.toString();
            String local = dependency.getNamespace().equals(chapterId().getNamespace()) && dependency.getPath().startsWith(chapterId().getPath() + "/")
                    ? dependency.getPath().substring(chapterId().getPath().length() + 1) : full;
            String s = per.getOrDefault(local, per.get(full));
            if (s != null && !s.isEmpty()) return s;
        }
        return def.layout.lineStyle;
    }

    public List<ResourceLocation> dependencies() {
        return dependencies;
    }

    public DependencyMode dependencyMode() {
        return def.logic.dependencyMode;
    }

    public int minDependencies() {
        return def.logic.minDependencies;
    }

    public Optional<Visibility> visibility() {
        return def.logic.visibility;
    }

    public boolean isOptional() {
        return def.logic.optional;
    }

    /**
     * Tasks have to be done in order: each one only counts once the one before it is complete.
     */
    public boolean sequentialTasks() {
        return def.logic.sequential;
    }

    public Repeat repeat() {
        return def.logic.repeat;
    }

    /**
     * Seconds the team has to finish the quest once it unlocks, or 0 for no limit. When time runs out the
     * quest's progress resets and the clock starts again.
     */
    public long timeLimitSeconds() {
        return def.logic.timeLimit;
    }

    /**
     * Quests that get locked forever once this one is completed, for "pick a path" branches.
     */
    public List<ResourceLocation> exclusiveWith() {
        return exclusiveWith;
    }

    public Optional<Boolean> autoClaim() {
        return def.logic.autoClaim;
    }

    public List<Task> tasks() {
        return def.logic.tasks;
    }

    public List<Reward> rewards() {
        return def.logic.rewards;
    }

    @Nullable
    public Task task(String taskId) {
        for (Task task : def.logic.tasks) {
            if (task.id().equals(taskId)) return task;
        }
        return null;
    }

    @Nullable
    public Reward reward(String rewardId) {
        for (Reward reward : def.logic.rewards) {
            if (reward.id().equals(rewardId)) return reward;
        }
        return null;
    }

    /**
     * Lang key for the title. Every bit of quest text gets one automatically, so packs can be translated
     * without the author doing anything; see the docs on the lang folder.
     */
    public String titleKey() {
        return key("title");
    }

    public String subtitleKey() {
        return key("subtitle");
    }

    public String descriptionKey() {
        return key("description");
    }

    public String hintKey() {
        return key("hint");
    }

    public String taskTitleKey(Task task) {
        return key("task." + task.id() + ".title");
    }

    public String taskDescriptionKey(Task task) {
        return key("task." + task.id() + ".description");
    }

    public String rewardTitleKey(Reward reward) {
        return key("reward." + reward.id() + ".title");
    }

    private String key(String suffix) {
        return "trailmark.quest." + id.getNamespace() + "." + id.getPath().replace('/', '.') + "." + suffix;
    }

    /**
     * Position of this quest in the book's flat quest list. Only meaningful once the book is built.
     */
    @ApiStatus.Internal
    public int index() {
        return index;
    }

    @ApiStatus.Internal
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Quest[" + id + "]";
    }

    /**
     * The quest exactly as written in its chapter file.
     */
    public record Def(Basics basics, Layout layout, Logic logic) {

        public static final Codec<Def> CODEC = Codec.mapPair(Basics.MAP_CODEC, Codec.mapPair(Layout.MAP_CODEC, Logic.MAP_CODEC))
                .xmap(p -> new Def(p.getFirst(), p.getSecond().getFirst(), p.getSecond().getSecond()),
                        d -> com.mojang.datafixers.util.Pair.of(d.basics, com.mojang.datafixers.util.Pair.of(d.layout, d.logic)))
                .codec();

        public Def withLayout(Layout newLayout) {
            return new Def(basics, newLayout, logic);
        }

        public Def withBasics(Basics newBasics) {
            return new Def(newBasics, layout, logic);
        }

        public Def withLogic(Logic newLogic) {
            return new Def(basics, layout, newLogic);
        }
    }

    public record Basics(String id, String title, String subtitle, String description, Icon icon, List<String> tags, String style, String hint) {
        public static final MapCodec<Basics> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.fieldOf("id").forGetter(Basics::id),
                Codec.STRING.optionalFieldOf("title", "").forGetter(Basics::title),
                Codec.STRING.optionalFieldOf("subtitle", "").forGetter(Basics::subtitle),
                TrailmarkCodecs.TEXT.optionalFieldOf("description", "").forGetter(Basics::description),
                Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(Basics::icon),
                Codec.STRING.listOf().optionalFieldOf("tags", List.of()).forGetter(Basics::tags),
                Codec.STRING.optionalFieldOf("style", "").forGetter(Basics::style),
                TrailmarkCodecs.TEXT.optionalFieldOf("hint", "").forGetter(Basics::hint)
        ).apply(i, Basics::new));
    }

    /**
     * Where and how the quest sits on the map.
     *
     * @param lineStyle  how lines into this quest are drawn: {@code elbow}, {@code straight}, or empty for the chapter's
     * @param lineStyles the same per dependency, keyed like {@code dependencies}
     * @param z          stacking order on the map: higher draws on top of lower, across quests and decorations alike
     *                   (quests without one are 0)
     */
    public record Layout(float x, float y, float size, Optional<NodeShape> shape, boolean hideDependencyLines, String lineStyle,
                         Map<String, String> lineStyles, Optional<Integer> z) {
        public static final MapCodec<Layout> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter(Layout::x),
                Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter(Layout::y),
                Codec.FLOAT.optionalFieldOf("size", 1.0F).forGetter(Layout::size),
                NodeShape.CODEC.optionalFieldOf("shape").forGetter(Layout::shape),
                Codec.BOOL.optionalFieldOf("hide_dependency_lines", false).forGetter(Layout::hideDependencyLines),
                Codec.STRING.optionalFieldOf("line_style", "").forGetter(Layout::lineStyle),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("line_styles", Map.of()).forGetter(Layout::lineStyles),
                Codec.INT.optionalFieldOf("z").forGetter(Layout::z)
        ).apply(i, Layout::new));

        public Layout(float x, float y, float size, Optional<NodeShape> shape, boolean hideDependencyLines) {
            this(x, y, size, shape, hideDependencyLines, "", Map.of(), Optional.empty());
        }

        public Layout(float x, float y, float size, Optional<NodeShape> shape, boolean hideDependencyLines, String lineStyle,
                      Map<String, String> lineStyles) {
            this(x, y, size, shape, hideDependencyLines, lineStyle, lineStyles, Optional.empty());
        }

        public Layout at(float nx, float ny) {
            return new Layout(nx, ny, size, shape, hideDependencyLines, lineStyle, lineStyles, z);
        }

        public Layout withSize(float nsize) {
            return new Layout(x, y, nsize, shape, hideDependencyLines, lineStyle, lineStyles, z);
        }

        public Layout withZ(Optional<Integer> nz) {
            return new Layout(x, y, size, shape, hideDependencyLines, lineStyle, lineStyles, nz);
        }
    }

    public record Logic(List<String> dependencies, DependencyMode dependencyMode, int minDependencies, Optional<Visibility> visibility,
                        boolean optional, boolean sequential, Repeat repeat, long timeLimit, List<String> exclusiveWith,
                        Optional<Boolean> autoClaim, List<Task> tasks, List<Reward> rewards) {
        public static final MapCodec<Logic> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                TrailmarkCodecs.singleOrList(Codec.STRING).optionalFieldOf("dependencies", List.of()).forGetter(Logic::dependencies),
                DependencyMode.CODEC.optionalFieldOf("dependency_mode", DependencyMode.ALL).forGetter(Logic::dependencyMode),
                Codec.INT.optionalFieldOf("min_dependencies", 0).forGetter(Logic::minDependencies),
                Visibility.CODEC.optionalFieldOf("visibility").forGetter(Logic::visibility),
                Codec.BOOL.optionalFieldOf("optional", false).forGetter(Logic::optional),
                Codec.BOOL.optionalFieldOf("sequential", false).forGetter(Logic::sequential),
                Repeat.CODEC.optionalFieldOf("repeat", Repeat.NEVER).forGetter(Logic::repeat),
                Codec.LONG.optionalFieldOf("time_limit", 0L).forGetter(Logic::timeLimit),
                TrailmarkCodecs.singleOrList(Codec.STRING).optionalFieldOf("exclusive_with", List.of()).forGetter(Logic::exclusiveWith),
                Codec.BOOL.optionalFieldOf("auto_claim").forGetter(Logic::autoClaim),
                TrailmarkRegistries.TASK_CODEC.listOf().optionalFieldOf("tasks", List.of()).forGetter(Logic::tasks),
                TrailmarkRegistries.REWARD_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Logic::rewards)
        ).apply(i, Logic::new));
    }
}
