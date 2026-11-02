package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.reward.RewardInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Book-wide settings from {@code book.json}.
 *
 * @param title          shown at the top of the quest screen
 * @param icon           shown next to the title and on the quest book item
 * @param groups         the chapter sidebar headings, in order
 * @param claimMode      default for rewards that don't say who they're for
 * @param autoClaim      default for quests that don't say whether rewards are handed out automatically
 * @param lockedMessage  tooltip for locked quests, markdown allowed; empty uses the built-in one
 * @param showLockIcons  draw a padlock on locked quests
 * @param gridSize       pixel spacing of one grid unit on the map at 100% zoom
 * @param startChapter   chapter the book opens on the first time; empty picks the first one
 * @param lineStyle      how dependency lines are drawn: {@code elbow} (default) or {@code straight}
 */
public record BookSettings(String title, Icon icon, List<ChapterGroup> groups, RewardInfo.ClaimMode claimMode, boolean autoClaim,
                           String lockedMessage, boolean showLockIcons, int gridSize, Optional<String> startChapter, String lineStyle,
                           int youtubeVolume) {

    public static final BookSettings DEFAULT = new BookSettings("", Icon.EMPTY, List.of(), RewardInfo.ClaimMode.PLAYER, false, "", true, 28, Optional.empty(), "", 50);

    public static final Codec<BookSettings> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.optionalFieldOf("title", "").forGetter(BookSettings::title),
            Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(BookSettings::icon),
            ChapterGroup.CODEC.listOf().optionalFieldOf("groups", List.of()).forGetter(BookSettings::groups),
            RewardInfo.ClaimMode.CODEC.optionalFieldOf("claim_mode", RewardInfo.ClaimMode.PLAYER).forGetter(BookSettings::claimMode),
            Codec.BOOL.optionalFieldOf("auto_claim", false).forGetter(BookSettings::autoClaim),
            Codec.STRING.optionalFieldOf("locked_message", "").forGetter(BookSettings::lockedMessage),
            Codec.BOOL.optionalFieldOf("show_lock_icons", true).forGetter(BookSettings::showLockIcons),
            Codec.intRange(8, 128).optionalFieldOf("grid_size", 28).forGetter(BookSettings::gridSize),
            Codec.STRING.optionalFieldOf("start_chapter").forGetter(BookSettings::startChapter),
            Codec.STRING.optionalFieldOf("line_style", "").forGetter(BookSettings::lineStyle),
            Codec.intRange(0, 100).optionalFieldOf("youtube_volume", 50).forGetter(BookSettings::youtubeVolume)
    ).apply(i, BookSettings::new));

    public Optional<ChapterGroup> group(String id) {
        for (ChapterGroup g : groups) {
            if (g.id().equals(id)) return Optional.of(g);
        }
        return Optional.empty();
    }
}
