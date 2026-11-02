package com.frikinjay.trailmark.api;

import com.frikinjay.trailmark.api.progress.ProgressView;
import com.frikinjay.trailmark.api.progress.StageProvider;
import com.frikinjay.trailmark.api.quest.Quest;
import com.frikinjay.trailmark.api.quest.QuestBook;
import com.frikinjay.trailmark.api.reward.Reward;
import com.frikinjay.trailmark.api.reward.RewardType;
import com.frikinjay.trailmark.api.signal.CustomSignal;
import com.frikinjay.trailmark.api.signal.Signal;
import com.frikinjay.trailmark.api.task.ItemSource;
import com.frikinjay.trailmark.api.task.Task;
import com.frikinjay.trailmark.api.task.TaskType;
import com.frikinjay.trailmark.api.team.TeamProvider;
import com.frikinjay.trailmark.core.engine.ItemSources;
import com.frikinjay.trailmark.core.engine.QuestEngine;
import com.frikinjay.trailmark.core.team.TeamManager;
import com.frikinjay.trailmark.network.Payloads;
import com.frikinjay.trailmark.network.TrailmarkNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The front door for other mods. Everything here is safe to call from the server thread; the register
 * methods belong in your mod's init.
 * <pre>{@code
 * // Fire a signal that "custom" tasks named "boss_defeated" listen for
 * TrailmarkApi.fireCustom(player, "boss_defeated", 1);
 *
 * // Read progress
 * ProgressView view = TrailmarkApi.progress(player);
 * QuestBook book = TrailmarkApi.book();
 * Quest quest = book.quest(ResourceLocation.parse("pack:getting_started/first_tree"));
 * if (quest != null && view.isComplete(quest)) { ... }
 * }</pre>
 * Client-side counterparts live in {@code com.frikinjay.trailmark.api.client.TrailmarkClientApi}.
 */
public final class TrailmarkApi {

    private static final List<StageProvider> STAGES = new CopyOnWriteArrayList<>();

    private TrailmarkApi() {}

    public static <T extends Task> TaskType<T> registerTaskType(TaskType<T> type) {
        return TrailmarkRegistries.register(type);
    }

    public static <T extends Reward> RewardType<T> registerRewardType(RewardType<T> type) {
        return TrailmarkRegistries.register(type);
    }

    public static void registerItemSource(ItemSource source) {
        ItemSources.register(source);
    }

    public static void registerTeamProvider(TeamProvider provider) {
        TeamManager.register(provider);
    }

    public static void registerStageProvider(StageProvider provider) {
        STAGES.add(provider);
    }

    /**
     * Whether Trailmark is running on this server right now.
     */
    public static boolean isRunning() {
        return QuestEngine.get() != null;
    }

    /**
     * The book the server is running, or an empty book when no world is loaded.
     */
    public static QuestBook book() {
        QuestEngine e = QuestEngine.get();
        return e == null ? QuestBook.EMPTY : e.book();
    }

    public static ProgressView progress(ServerPlayer player) {
        return engine(player.server).view(player);
    }

    public static UUID teamOf(ServerPlayer player) {
        return TeamManager.teamOf(player);
    }

    /**
     * Hand a signal to the tasks listening for it.
     */
    public static void signal(Signal signal) {
        QuestEngine.of(signal.player().server).ifPresent(e -> e.fire(signal));
    }

    public static void fireCustom(ServerPlayer player, String name, long amount) {
        signal(new CustomSignal(player, name, amount));
    }

    /**
     * Sets a task's progress for the player's team. Returns false when the quest or task doesn't exist.
     */
    public static boolean setTaskProgress(ServerPlayer player, ResourceLocation questId, String taskId, long value) {
        QuestEngine e = engine(player.server);
        Quest quest = e.book().quest(questId);
        Task task = quest == null ? null : quest.task(taskId);
        if (task == null) return false;
        e.setProgress(TeamManager.teamOf(player), quest, task, value, player);
        return true;
    }

    public static boolean completeQuest(MinecraftServer server, UUID team, ResourceLocation questId) {
        QuestEngine e = engine(server);
        Quest quest = e.book().quest(questId);
        if (quest == null) return false;
        e.forceComplete(team, quest, null);
        return true;
    }

    public static boolean resetQuest(MinecraftServer server, UUID team, ResourceLocation questId) {
        QuestEngine e = engine(server);
        Quest quest = e.book().quest(questId);
        if (quest == null) return false;
        e.resetQuest(team, quest);
        return true;
    }

    public static boolean hasStage(ServerPlayer player, String stage) {
        QuestEngine e = QuestEngine.of(player.server).orElse(null);
        if (e != null && e.team(TeamManager.teamOf(player)).stages().contains(stage)) return true;
        for (StageProvider p : STAGES) {
            try {
                if (p.hasStage(player, stage)) return true;
            } catch (RuntimeException | LinkageError ignored) {
            }
        }
        return false;
    }

    /**
     * Adds or removes a stage on the player's team, and mirrors it to any stage mod that accepts writes.
     */
    public static void setStage(ServerPlayer player, String stage, boolean present) {
        engine(player.server).setStage(TeamManager.teamOf(player), stage, present);
        for (StageProvider p : STAGES) {
            try {
                p.setStage(player, stage, present);
            } catch (RuntimeException | LinkageError ignored) {
            }
        }
        for (ServerPlayer member : TeamManager.online(player.server, TeamManager.teamOf(player))) {
            fireCustom(member, "stage:" + stage, 1);
        }
    }

    public static long counter(MinecraftServer server, UUID team, String name) {
        return QuestEngine.of(server).map(e -> e.team(team).counter(name)).orElse(0L);
    }

    public static void setCounter(MinecraftServer server, UUID team, String name, long value) {
        QuestEngine e = engine(server);
        e.setCounter(team, name, value);
        for (ServerPlayer member : TeamManager.online(server, team)) fireCustom(member, "counter:" + name, 0);
    }

    public static void addCounter(MinecraftServer server, UUID team, String name, long amount) {
        setCounter(server, team, name, counter(server, team, name) + amount);
    }

    /**
     * Opens the quest book on the player's screen, on a quest if you give one.
     */
    public static void openBook(ServerPlayer player, @Nullable ResourceLocation quest) {
        TrailmarkNetwork.send(player, new Payloads.OpenBook(Optional.ofNullable(quest)));
    }

    private static QuestEngine engine(MinecraftServer server) {
        return QuestEngine.of(server).orElseThrow(() -> new IllegalStateException("Trailmark isn't running on this server"));
    }
}
