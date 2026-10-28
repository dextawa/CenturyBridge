package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.class_1299;
import net.minecraft.class_5483;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;

/**
 * The weight 1.21.4's SpawnerData carried itself and 1.21.5 moved out of it.
 *
 * 1.21.4 MobSpawnSettings.SpawnerData (class_5483$class_1964) extends WeightedEntry.IntrusiveBase(type, Weight, min,
 * max): the spawn lists hold the SpawnerData, and getWeight() answers its own Weight. 1.21.5 made it a record (type,
 * min, max) and keeps the weight in the list's Weighted(SpawnerData, int) entry (javap 1.21.5 class_5483$class_1964,
 * class_5483$class_5496.addSpawn(MobCategory, int, SpawnerData)).
 *
 * A converted mod still reads a SpawnerData's weight from the SpawnerData. This side table, keyed by identity and
 * holding its keys weakly, carries it: every SpawnerData that enters a 1.21.5 weighted list is recorded with its
 * entry's weight (rt/CbWeighted, mixin/CbWeightedListMixin), so are the ones a converted mod constructs (with the weight
 * it passed) and the ones the layer hands a mod out of a list (the entry it got wins). 1.21.5 builds every vanilla
 * SpawnerData straight into exactly one entry (javap 1.21.5: each `new SpawnerData` in BiomeDefaultFeatures,
 * NetherFortressStructure, OverworldBiomes, NetherBiomes, EndBiomes, Biome... is consumed by its single
 * addSpawn / builder add; the codec decodes a fresh one per entry), so one instance never stands in two entries of
 * different weights there.
 */
public final class CbSpawnWeights {
    private CbSpawnWeights() {
    }

    /** weak keys compare by identity (MapMaker) */
    private static final Map<class_5483.class_1964, Integer> WEIGHTS = new MapMaker().weakKeys().concurrencyLevel(4).makeMap();

    /**
     * The weight answered for a SpawnerData no 1.21.5 list, mod constructor or layer hand-out has carried. 1.21.4 has
     * no answer for it (every 1.21.4 SpawnerData was built with its weight), so 1 is a documented loss (recorded in
     * D:/tmp/main/release_notes_known.md). Only a SpawnerData that never stood in a weighted list reaches it.
     */
    public static final int UNSEEN_WEIGHT = 1;

    public static void record(class_5483.class_1964 data, int weight) {
        if (data != null) {
            WEIGHTS.put(data, weight);
        }
    }

    public static int weight(class_5483.class_1964 data) {
        Integer w = WEIGHTS.get(data);
        return w != null ? w : UNSEEN_WEIGHT;
    }

    /** the int weight 1.21.5's addSpawn takes beside the data */
    public static int weightOf(class_5483.class_1964 data) {
        return weight(data);
    }

    /** new SpawnerData(type, int weight, min, max): 1.21.4's int constructor went through Weight.of */
    public static class_5483.class_1964 newSpawnerData(class_1299<?> type, int weight, int minCount, int maxCount) {
        return newSpawnerData(type, Weight.method_34977(weight), minCount, maxCount);
    }

    /** new SpawnerData(type, Weight, min, max): 1.21.5's record (MISC -> PIG as 1.21.4's constructor) carrying the weight */
    public static class_5483.class_1964 newSpawnerData(class_1299<?> type, Weight weight, int minCount, int maxCount) {
        class_5483.class_1964 data = new class_5483.class_1964(type, minCount, maxCount);
        record(data, weight.method_34976());
        return data;
    }

    /** SpawnerData.type */
    public static class_1299<?> field_9389(class_5483.class_1964 data) {
        return data.comp_3488();
    }

    /** SpawnerData.minCount */
    public static int field_9388(class_5483.class_1964 data) {
        return data.comp_3489();
    }

    /** SpawnerData.maxCount */
    public static int field_9387(class_5483.class_1964 data) {
        return data.comp_3490();
    }

    private static final Codec<class_5483.class_1964> CODEC = RecordCodecBuilder.create(i -> i.group(
            class_5483.class_1964.field_24681.forGetter(d -> d),
            Weight.field_29927.fieldOf("weight").forGetter(d -> Weight.method_34977(weight(d))))
        .apply(i, (d, w) -> {
            // 1.21.5's MapCodec decodes a fresh record, as 1.21.4's codec built a fresh SpawnerData
            record(d, w.method_34976());
            return d;
        }));

    /** SpawnerData.CODEC: {type, weight, minCount, maxCount} with minCount <= maxCount checked (1.21.5's MapCodec checks it) */
    public static Codec<class_5483.class_1964> field_24681() {
        return CODEC;
    }
}
