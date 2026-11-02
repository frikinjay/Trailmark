package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.TrailmarkRegistries;
import com.frikinjay.trailmark.api.reward.Reward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * A weighted list of rewards that {@code random} rewards roll from. Lives in its own file under
 * {@code tables/}, so many quests can share one.
 *
 * @param id          the table id, from its file name
 * @param title       shown on rewards that roll from it
 * @param icon        shown on rewards that roll from it
 * @param rolls       how many entries are picked per claim
 * @param emptyWeight weight of rolling nothing at all
 * @param entries     the rewards and their weights
 */
public record RewardTable(ResourceLocation id, String title, Icon icon, int rolls, int emptyWeight, List<Entry> entries) {

    public record Entry(int weight, Reward reward) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.optionalFieldOf("weight", 1).forGetter(Entry::weight),
                TrailmarkRegistries.REWARD_CODEC.fieldOf("reward").forGetter(Entry::reward)
        ).apply(i, Entry::new));
    }

    /**
     * The table as written in its file, without the id.
     */
    public record Def(String title, Icon icon, int rolls, int emptyWeight, List<Entry> entries) {
        public static final Codec<Def> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("title", "").forGetter(Def::title),
                Icon.CODEC.optionalFieldOf("icon", Icon.EMPTY).forGetter(Def::icon),
                Codec.INT.optionalFieldOf("rolls", 1).forGetter(Def::rolls),
                Codec.INT.optionalFieldOf("empty_weight", 0).forGetter(Def::emptyWeight),
                Entry.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(Def::entries)
        ).apply(i, Def::new));

        public RewardTable withId(ResourceLocation id) {
            return new RewardTable(id, title, icon, rolls, emptyWeight, entries);
        }
    }

    public Def def() {
        return new Def(title, icon, rolls, emptyWeight, entries);
    }

    public int totalWeight() {
        int total = Math.max(0, emptyWeight);
        for (Entry e : entries) total += Math.max(0, e.weight);
        return total;
    }

    /**
     * Picks {@link #rolls} entries, skipping empty rolls.
     */
    public List<Reward> roll(RandomSource random) {
        List<Reward> out = new ArrayList<>();
        int total = totalWeight();
        if (total <= 0) return out;
        for (int r = 0; r < Math.max(1, rolls); r++) {
            int pick = random.nextInt(total);
            if (pick < emptyWeight) continue;
            pick -= Math.max(0, emptyWeight);
            for (Entry e : entries) {
                pick -= Math.max(0, e.weight);
                if (pick < 0) {
                    out.add(e.reward);
                    break;
                }
            }
        }
        return out;
    }

    public String titleKey() {
        return "trailmark.table." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".title";
    }
}
