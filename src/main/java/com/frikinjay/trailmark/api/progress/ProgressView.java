package com.frikinjay.trailmark.api.progress;

import com.frikinjay.trailmark.api.quest.Chapter;
import com.frikinjay.trailmark.api.quest.Quest;
import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.task.Task;

import java.util.Set;
import java.util.UUID;

/**
 * Read-only progress for one team, seen through one player's eyes. On the server you can get one for
 * any online player; on the client there's one for the local player.
 */
public interface ProgressView {

    UUID teamId();

    UUID playerId();

    QuestStatus status(Quest quest);

    long progress(Quest quest, Task task);

    default boolean isTaskComplete(Quest quest, Task task) {
        return progress(quest, task) >= task.target();
    }

    /**
     * Whether every required task is done, regardless of rewards.
     */
    boolean isComplete(Quest quest);

    boolean isClaimed(Quest quest, Reward reward);

    boolean isChapterRewardClaimed(Chapter chapter, Reward reward);

    /**
     * When the quest was last completed, in epoch milliseconds, or 0.
     */
    long completedAt(Quest quest);

    /**
     * When the current attempt at a timed quest started, in epoch milliseconds, or 0.
     */
    long startedAt(Quest quest);

    int timesCompleted(Quest quest);

    /**
     * 0 to 1, how much of a chapter's required quests are complete.
     */
    float chapterProgress(Chapter chapter);

    boolean isChapterUnlocked(Chapter chapter);

    boolean hasStage(String stage);

    Set<String> stages();

    long counter(String name);
}
