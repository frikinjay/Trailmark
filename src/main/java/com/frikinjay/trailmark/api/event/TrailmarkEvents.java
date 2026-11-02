package com.frikinjay.trailmark.api.event;

import com.frikinjay.trailmark.api.quest.Chapter;
import com.frikinjay.trailmark.api.quest.Quest;
import com.frikinjay.trailmark.api.quest.QuestBook;
import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.task.Task;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Hooks into the quest lifecycle. Everything here fires on the server thread.
 * <pre>{@code
 * TrailmarkEvents.QUEST_COMPLETED.register((server, team, quest, player) ->
 *         LOGGER.info("{} finished {}", team, quest.id()));
 * }</pre>
 */
public final class TrailmarkEvents {

    private TrailmarkEvents() {}

    /** A new book was loaded, on server start, {@code /reload} or an editor save. */
    public static final Event<BookLoaded> BOOK_LOADED = Event.create(list -> (server, book) -> {
        for (BookLoaded l : list) l.onBookLoaded(server, book);
    });

    /** A quest's dependencies were just met for a team. */
    public static final Event<QuestUnlocked> QUEST_UNLOCKED = Event.create(list -> (server, team, quest) -> {
        for (QuestUnlocked l : list) l.onQuestUnlocked(server, team, quest);
    });

    /** A task reached its target. {@code player} is whoever did the last bit, or null. */
    public static final Event<TaskCompleted> TASK_COMPLETED = Event.create(list -> (server, team, quest, task, player) -> {
        for (TaskCompleted l : list) l.onTaskCompleted(server, team, quest, task, player);
    });

    /** Every required task of a quest is done. */
    public static final Event<QuestCompleted> QUEST_COMPLETED = Event.create(list -> (server, team, quest, player) -> {
        for (QuestCompleted l : list) l.onQuestCompleted(server, team, quest, player);
    });

    /** Every required quest of a chapter is done. */
    public static final Event<ChapterCompleted> CHAPTER_COMPLETED = Event.create(list -> (server, team, chapter) -> {
        for (ChapterCompleted l : list) l.onChapterCompleted(server, team, chapter);
    });

    /** Fired before a reward is handed out. Return false from any listener to stop it. */
    public static final Event<BeforeClaim> BEFORE_CLAIM = Event.create(list -> (player, quest, reward) -> {
        for (BeforeClaim l : list) {
            if (!l.canClaim(player, quest, reward)) return false;
        }
        return true;
    });

    /** A reward was handed out. {@code quest} is null for chapter rewards. */
    public static final Event<RewardClaimed> REWARD_CLAIMED = Event.create(list -> (player, quest, reward) -> {
        for (RewardClaimed l : list) l.onRewardClaimed(player, quest, reward);
    });

    /** A quest's progress was wiped for a team, by a command, a repeat or a timer running out. */
    public static final Event<QuestReset> QUEST_RESET = Event.create(list -> (server, team, quest) -> {
        for (QuestReset l : list) l.onQuestReset(server, team, quest);
    });

    @FunctionalInterface
    public interface BookLoaded {
        void onBookLoaded(MinecraftServer server, QuestBook book);
    }

    @FunctionalInterface
    public interface QuestUnlocked {
        void onQuestUnlocked(MinecraftServer server, UUID team, Quest quest);
    }

    @FunctionalInterface
    public interface TaskCompleted {
        void onTaskCompleted(MinecraftServer server, UUID team, Quest quest, Task task, @Nullable ServerPlayer player);
    }

    @FunctionalInterface
    public interface QuestCompleted {
        void onQuestCompleted(MinecraftServer server, UUID team, Quest quest, @Nullable ServerPlayer player);
    }

    @FunctionalInterface
    public interface ChapterCompleted {
        void onChapterCompleted(MinecraftServer server, UUID team, Chapter chapter);
    }

    @FunctionalInterface
    public interface BeforeClaim {
        boolean canClaim(ServerPlayer player, @Nullable Quest quest, Reward reward);
    }

    @FunctionalInterface
    public interface RewardClaimed {
        void onRewardClaimed(ServerPlayer player, @Nullable Quest quest, Reward reward);
    }

    @FunctionalInterface
    public interface QuestReset {
        void onQuestReset(MinecraftServer server, UUID team, Quest quest);
    }
}
