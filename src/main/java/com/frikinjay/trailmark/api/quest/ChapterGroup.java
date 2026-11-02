package com.frikinjay.trailmark.api.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A heading in the chapter sidebar that chapters can be filed under. Chapters without a group sit at the top.
 *
 * @param id        what chapters put in their {@code "group"} field
 * @param title     shown in the sidebar
 * @param icon      shown next to the title
 * @param collapsed whether the group starts folded
 */
public record ChapterGroup(String id, String title, Icon icon, boolean collapsed) {

    public static final Codec<ChapterGroup> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(ChapterGroup::id),
            Codec.STRING.optionalFieldOf("title", "").forGetter(ChapterGroup::title),
            Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(ChapterGroup::icon),
            Codec.BOOL.optionalFieldOf("collapsed", false).forGetter(ChapterGroup::collapsed)
    ).apply(i, ChapterGroup::new));

    public String titleKey() {
        return "trailmark.group." + id.replace('/', '.').replace(':', '.') + ".title";
    }
}
