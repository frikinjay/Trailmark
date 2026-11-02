package com.frikinjay.trailmark.api.task;

import com.frikinjay.trailmark.api.quest.Icon;
import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * The fields every task has, whatever its type. They sit next to the type's own fields in the file, so
 * avoid these names in your own task codecs: {@code type}, {@code id}, {@code title}, {@code description},
 * {@code icon}, {@code optional}, {@code hidden}.
 *
 * @param id          unique within the quest; progress is saved against it, so keep it stable. Left empty,
 *                    Trailmark uses the task's position ({@code "0"}, {@code "1"}, ...)
 * @param title       custom title, or empty to use the type's generated one
 * @param description extra text shown under the task, markdown allowed
 * @param icon        custom icon, or empty to use the type's own
 * @param optional    optional tasks don't block completing the quest
 * @param hidden      hidden tasks aren't listed until they're done
 */
public record TaskInfo(String id, String title, String description, Icon icon, boolean optional, boolean hidden) {

    public static final TaskInfo DEFAULT = new TaskInfo("", "", "", Icon.EMPTY, false, false);

    public static final MapCodec<TaskInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("id", "").forGetter(TaskInfo::id),
            Codec.STRING.optionalFieldOf("title", "").forGetter(TaskInfo::title),
            TrailmarkCodecs.TEXT.optionalFieldOf("description", "").forGetter(TaskInfo::description),
            Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(TaskInfo::icon),
            Codec.BOOL.optionalFieldOf("optional", false).forGetter(TaskInfo::optional),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(TaskInfo::hidden)
    ).apply(i, TaskInfo::new));

    public TaskInfo withId(String newId) {
        return new TaskInfo(newId, title, description, icon, optional, hidden);
    }
}
