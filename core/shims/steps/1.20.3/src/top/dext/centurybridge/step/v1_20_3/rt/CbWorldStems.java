package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;
import net.minecraft.class_1792;
import net.minecraft.class_2195;
import net.minecraft.class_2248;
import net.minecraft.class_2513;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_20_3.facade.StemGrownBlock;

/**
 * Stems: 1.20.2's StemBlock / AttachedStemBlock held their fruit (a StemGrownBlock) and a seed
 * supplier; 1.20.3's name fruit, stem and seed by registry key, resolved through the level's
 * registries. A 1.20.2 constructor runs before anything it names is registered, so no key can be
 * computed then. The reshaped constructor call gives the 1.20.3 fields keys of nothing, and its post
 * step hands the 1.20.2 fruit and seed to the constructed stem (CbWorldStemMixin /
 * CbWorldAttachedStemMixin); such a stem grows, attaches, reverts and picks its seed through the
 * objects, as 1.20.2 did.
 */
public final class CbWorldStems {

    /** what a 1.20.2 stem constructor was given */
    public record LegacyStem(StemGrownBlock fruit, Supplier<class_1792> seed) {
    }

    /** keys no registry holds: the 1.20.3 key fields of a stem built the 1.20.2 way are never looked up */
    private static final class_5321<class_2248> NO_BLOCK =
        class_5321.method_29179(class_7924.field_41254, new class_2960("centurybridge", "legacy_stem_target"));
    private static final class_5321<class_1792> NO_ITEM =
        class_5321.method_29179(class_7924.field_41197, new class_2960("centurybridge", "legacy_stem_seed"));

    public static class_5321<class_2248> noBlock() {
        return NO_BLOCK;
    }

    public static class_5321<class_1792> noItem() {
        return NO_ITEM;
    }

    /** post step of StemBlock(fruit, seed, props): the stem keeps the 1.20.2 fruit and seed */
    public static void attachStem(class_2513 stem, StemGrownBlock fruit, Supplier<class_1792> seed) {
        ((CbWorldStemAccess) stem).cb$setLegacyStem(new LegacyStem(fruit, seed));
    }

    /** post step of AttachedStemBlock(fruit, seed, props), as {@link #attachStem} */
    public static void attachAttachedStem(class_2195 stem, StemGrownBlock fruit, Supplier<class_1792> seed) {
        ((CbWorldStemAccess) stem).cb$setLegacyStem(new LegacyStem(fruit, seed));
    }

    private CbWorldStems() {
    }
}
