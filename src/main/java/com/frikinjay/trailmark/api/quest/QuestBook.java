package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.task.Task;
import com.frikinjay.trailmark.reward.UnknownReward;
import com.frikinjay.trailmark.task.UnknownTask;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The whole quest book as loaded: settings, chapters, quests and reward tables. A new instance is built on
 * every reload and swapped in atomically, so readers never see a half-loaded book.
 */
public final class QuestBook {

    public static final QuestBook EMPTY = build(BookSettings.DEFAULT, Map.of(), Map.of(), "", List.of());

    private final BookSettings settings;
    private final List<Chapter> chapters;
    private final Map<ResourceLocation, Chapter> chaptersById;
    private final List<Quest> quests;
    private final Map<ResourceLocation, Quest> questsById;
    private final Map<ResourceLocation, List<Quest>> dependents;
    private final Map<ResourceLocation, RewardTable> tables;
    private final String style;
    private final List<Problem> problems;

    private QuestBook(BookSettings settings, List<Chapter> chapters, Map<ResourceLocation, RewardTable> tables, String style, List<Problem> problems) {
        this.settings = settings;
        this.chapters = List.copyOf(chapters);
        this.tables = Collections.unmodifiableMap(new LinkedHashMap<>(tables));
        this.style = style;
        Map<ResourceLocation, Chapter> byId = new LinkedHashMap<>();
        Map<ResourceLocation, Quest> questMap = new LinkedHashMap<>();
        List<Quest> flat = new ArrayList<>();
        for (Chapter chapter : this.chapters) {
            byId.put(chapter.id(), chapter);
            for (Quest quest : chapter.quests()) {
                quest.setIndex(flat.size());
                flat.add(quest);
                questMap.put(quest.id(), quest);
            }
        }
        this.chaptersById = Collections.unmodifiableMap(byId);
        this.quests = List.copyOf(flat);
        this.questsById = Collections.unmodifiableMap(questMap);
        Map<ResourceLocation, List<Quest>> deps = new HashMap<>();
        for (Quest quest : flat) {
            for (ResourceLocation dep : quest.dependencies()) deps.computeIfAbsent(dep, k -> new ArrayList<>()).add(quest);
        }
        deps.replaceAll((k, v) -> List.copyOf(v));
        this.dependents = Collections.unmodifiableMap(deps);
        this.problems = new ArrayList<>(problems);
    }

    /**
     * Builds a book from parsed files. Quests get stable task and reward ids, broken references are dropped
     * and reported, and dependency loops are cut so the book can always load.
     *
     * @param chapterDefs chapter files by chapter id
     * @param tableDefs   reward table files by table id
     * @param style       the quest pack's stylesheet
     * @param loadProblems problems found while reading the files
     */
    public static QuestBook build(BookSettings settings, Map<ResourceLocation, Chapter.Def> chapterDefs, Map<ResourceLocation, RewardTable.Def> tableDefs,
                                  String style, List<Problem> loadProblems) {
        List<Problem> problems = new ArrayList<>(loadProblems);
        List<Map.Entry<ResourceLocation, Chapter.Def>> sorted = new ArrayList<>(chapterDefs.entrySet());
        Map<String, Integer> groupOrder = new HashMap<>();
        for (int i = 0; i < settings.groups().size(); i++) groupOrder.put(settings.groups().get(i).id(), i);
        sorted.sort(Comparator.<Map.Entry<ResourceLocation, Chapter.Def>>comparingInt(e -> e.getValue().basics().group().isEmpty() ? -1
                        : groupOrder.getOrDefault(e.getValue().basics().group(), Integer.MAX_VALUE))
                .thenComparingInt(e -> e.getValue().basics().order())
                .thenComparing(e -> e.getKey().toString()));

        Set<ResourceLocation> seenQuests = new HashSet<>();
        List<Chapter> chapters = new ArrayList<>();
        for (var entry : sorted) {
            ResourceLocation chapterId = entry.getKey();
            List<Quest> quests = new ArrayList<>();
            for (Quest.Def def : entry.getValue().quests()) {
                Quest quest = new Quest(chapterId, def);
                if (!seenQuests.add(quest.id())) {
                    problems.add(new Problem(quest.id().toString(), "Duplicate quest id, the second copy was skipped"));
                    continue;
                }
                normaliseIds(quest, problems);
                quests.add(quest);
            }
            for (int i = 0; i < entry.getValue().logic().rewards().size(); i++) {
                Reward reward = entry.getValue().logic().rewards().get(i);
                if (reward.id().isEmpty()) reward.attachInfo(reward.info().withId(Integer.toString(i)));
            }
            chapters.add(new Chapter(chapterId, entry.getValue(), quests));
        }

        Map<ResourceLocation, RewardTable> tables = new LinkedHashMap<>();
        tableDefs.forEach((id, def) -> tables.put(id, def.withId(id)));

        QuestBook book = new QuestBook(settings, chapters, tables, style, problems);
        book.validate();
        return book;
    }

    private static void normaliseIds(Quest quest, List<Problem> problems) {
        Set<String> taskIds = new HashSet<>();
        List<Task> tasks = quest.tasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.id().isEmpty()) task.attachInfo(task.info().withId(Integer.toString(i)));
            if (!taskIds.add(task.id())) {
                String fresh = task.id() + "_" + i;
                problems.add(new Problem(quest.id().toString(), "Two tasks share the id '" + task.id() + "', renamed the second to '" + fresh + "'"));
                task.attachInfo(task.info().withId(fresh));
                taskIds.add(fresh);
            }
            if (task instanceof UnknownTask unknown) problems.add(new Problem(quest.id() + " task " + task.id(), unknown.error()));
        }
        Set<String> rewardIds = new HashSet<>();
        List<Reward> rewards = quest.rewards();
        for (int i = 0; i < rewards.size(); i++) {
            Reward reward = rewards.get(i);
            if (reward.id().isEmpty()) reward.attachInfo(reward.info().withId(Integer.toString(i)));
            if (!rewardIds.add(reward.id())) {
                String fresh = reward.id() + "_" + i;
                problems.add(new Problem(quest.id().toString(), "Two rewards share the id '" + reward.id() + "', renamed the second to '" + fresh + "'"));
                reward.attachInfo(reward.info().withId(fresh));
                rewardIds.add(fresh);
            }
            if (reward instanceof UnknownReward unknown) problems.add(new Problem(quest.id() + " reward " + reward.id(), unknown.error()));
        }
    }

    private void validate() {
        for (Quest quest : quests) {
            for (ResourceLocation dep : quest.dependencies()) {
                if (!questsById.containsKey(dep)) problems.add(new Problem(quest.id().toString(), "Depends on " + dep + ", which doesn't exist"));
            }
        }
        for (Chapter chapter : chapters) {
            for (ResourceLocation req : chapter.requires()) {
                if (!questsById.containsKey(req) && !chaptersById.containsKey(req)) {
                    problems.add(new Problem(chapter.id().toString(), "Requires " + req + ", which is neither a quest nor a chapter"));
                }
            }
        }
        for (List<ResourceLocation> loop : findLoops()) {
            problems.add(new Problem(loop.get(0).toString(), "Dependency loop: " + loop + ". These quests can never unlock"));
        }
    }

    private List<List<ResourceLocation>> findLoops() {
        List<List<ResourceLocation>> loops = new ArrayList<>();
        int[] state = new int[quests.size()];
        for (Quest start : quests) {
            if (state[start.index()] != 0) continue;
            List<Quest> stack = new ArrayList<>();
            List<Integer> cursor = new ArrayList<>();
            stack.add(start);
            cursor.add(0);
            state[start.index()] = 1;
            while (!stack.isEmpty()) {
                int top = stack.size() - 1;
                Quest q = stack.get(top);
                int c = cursor.get(top);
                if (c < q.dependencies().size()) {
                    cursor.set(top, c + 1);
                    Quest next = questsById.get(q.dependencies().get(c));
                    if (next == null) continue;
                    if (state[next.index()] == 1) {
                        List<ResourceLocation> loop = new ArrayList<>();
                        for (int i = stack.indexOf(next); i < stack.size(); i++) loop.add(stack.get(i).id());
                        loops.add(loop);
                    } else if (state[next.index()] == 0) {
                        state[next.index()] = 1;
                        stack.add(next);
                        cursor.add(0);
                    }
                } else {
                    state[q.index()] = 2;
                    stack.remove(top);
                    cursor.remove(top);
                }
            }
        }
        return loops;
    }

    public BookSettings settings() {
        return settings;
    }

    /**
     * Chapters in display order: ungrouped first, then by group order, then by each chapter's {@code order}.
     */
    public List<Chapter> chapters() {
        return chapters;
    }

    @Nullable
    public Chapter chapter(ResourceLocation id) {
        return chaptersById.get(id);
    }

    /**
     * Every quest across all chapters, in chapter order.
     */
    public List<Quest> quests() {
        return quests;
    }

    @Nullable
    public Quest quest(ResourceLocation id) {
        return questsById.get(id);
    }

    @Nullable
    public Quest quest(int index) {
        return index >= 0 && index < quests.size() ? quests.get(index) : null;
    }

    /**
     * Quests that list {@code id} as a dependency.
     */
    public List<Quest> dependents(ResourceLocation id) {
        return dependents.getOrDefault(id, List.of());
    }

    public Map<ResourceLocation, RewardTable> tables() {
        return tables;
    }

    @Nullable
    public RewardTable table(ResourceLocation id) {
        return tables.get(id);
    }

    /**
     * The quest pack's own stylesheet, layered over the resource pack theme.
     */
    public String style() {
        return style;
    }

    /**
     * Everything that looked wrong while loading. The editor lists these, and {@code /trailmark problems} prints them.
     */
    public List<Problem> problems() {
        return Collections.unmodifiableList(problems);
    }

    public boolean isEmpty() {
        return quests.isEmpty() && chapters.isEmpty();
    }

    /**
     * Something odd in the quest files.
     *
     * @param where   the quest, chapter or file it's about
     * @param message what's wrong, in plain words
     */
    public record Problem(String where, String message) {
    }
}
