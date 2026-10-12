package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Optional;

/**
 * 1.20.4's LocationPredicate API on 1.20.5's: 1.20.4 matched one biome / structure by key, 1.20.5 holds a
 * set of them. A 1.20.4 key becomes the one-key set CbItemGenerics.KeySet (matching by key as 1.20.4 did,
 * whichever registry the holder comes from); a set handed to 1.20.4 code answers its key -- a KeySet's own,
 * else its first entry's. Also VanillaFishingLoot's jungle conditions, which 1.20.4 kept as constants.
 */
public final class CbPredicates {

    /** a 1.20.4 biome key as 1.20.5's set */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6885 biomeKeySet(net.minecraft.class_5321 key) {
        return (net.minecraft.class_6885) CbItemGenerics.biomeSet(Optional.of(key)).get();
    }

    /** a 1.20.4 structure key as 1.20.5's set */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6885 structureKeySet(net.minecraft.class_5321 key) {
        return (net.minecraft.class_6885) CbItemGenerics.structureSet(Optional.of(key)).get();
    }

    /** the key 1.20.4 code sees for a set: a KeySet's own, else its first entry's (null for an empty set) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5321 keyOf(net.minecraft.class_6885 set) {
        if (set == null) {
            return null;
        }
        if (set instanceof CbItemGenerics.KeySet k) {
            return k.key();
        }
        java.util.Iterator<net.minecraft.class_6880> it = set.iterator();
        return it.hasNext() ? (net.minecraft.class_5321) it.next().method_40230().orElse(null) : null;
    }

    /** locationPredicate.biome(): the biome key 1.20.4's record held */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional biome(net.minecraft.class_2090 p) {
        return p.comp_2375().map(s -> keyOf((net.minecraft.class_6885) s));
    }

    /** locationPredicate.structure() */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional structure(net.minecraft.class_2090 p) {
        return p.comp_2376().map(s -> keyOf((net.minecraft.class_6885) s));
    }

    /** LocationPredicate.Builder.inBiome(key): a builder matching that biome */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2090.class_2091 inBiome(net.minecraft.class_5321 key) {
        return net.minecraft.class_2090.class_2091.method_22484().method_9024(biomeKeySet(key));
    }

    /** LocationPredicate.Builder.inStructure(key) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2090.class_2091 inStructure(net.minecraft.class_5321 key) {
        return net.minecraft.class_2090.class_2091.method_22484().method_35277(structureKeySet(key));
    }

    // ---------------------------------------------------------------- VanillaFishingLoot's constants

    private static net.minecraft.class_5341.class_210 inJungle;
    private static net.minecraft.class_5341.class_210 inSparseJungle;
    private static net.minecraft.class_5341.class_210 inBambooJungle;

    private static net.minecraft.class_5341.class_210 inBiomeCondition(net.minecraft.class_5321 biome) {
        return net.minecraft.class_205.method_884(inBiome(biome));
    }

    /** VanillaFishingLoot.IN_JUNGLE: 1.20.4's location check for the jungle */
    public static net.minecraft.class_5341.class_210 inJungle() {
        if (inJungle == null) {
            inJungle = inBiomeCondition(net.minecraft.class_1972.field_9417);
        }
        return inJungle;
    }

    /** VanillaFishingLoot.IN_SPARSE_JUNGLE */
    public static net.minecraft.class_5341.class_210 inSparseJungle() {
        if (inSparseJungle == null) {
            inSparseJungle = inBiomeCondition(net.minecraft.class_1972.field_35118);
        }
        return inSparseJungle;
    }

    /** VanillaFishingLoot.IN_BAMBOO_JUNGLE */
    public static net.minecraft.class_5341.class_210 inBambooJungle() {
        if (inBambooJungle == null) {
            inBambooJungle = inBiomeCondition(net.minecraft.class_1972.field_9440);
        }
        return inBambooJungle;
    }

    private CbPredicates() {
    }
}
