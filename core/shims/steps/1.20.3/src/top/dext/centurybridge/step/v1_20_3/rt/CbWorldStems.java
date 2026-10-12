package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_20_3.facade.StemGrownBlock;

/**
 * Stems: 1.20.2's StemBlock / AttachedStemBlock held their fruit (a StemGrownBlock) and a seed
 * supplier; 1.20.3's name fruit, stem and seed by registry key, resolved through the level's
 * registries. A 1.20.2 constructor runs before anything it names is registered, so no key can be
 * computed then. The reshaped constructor call hands the 1.20.2 fruit and seed to the stem under
 * construction (CbWorldStemMixin / CbWorldAttachedStemMixin take them when the constructor returns)
 * and gives the 1.20.3 fields keys of nothing; such a stem grows, attaches, reverts and picks its
 * seed through the objects, as 1.20.2 did.
 */
public final class CbWorldStems {

    /** what a 1.20.2 stem constructor was given */
    public record LegacyStem(StemGrownBlock fruit, Supplier<class_1792> seed) {
    }

    private static final ThreadLocal<LegacyStem> PENDING = new ThreadLocal<>();

    /** keys no registry holds: the 1.20.3 key fields of a stem built the 1.20.2 way are never looked up */
    private static final class_5321<class_2248> NO_BLOCK =
        class_5321.method_29179(class_7924.field_41254, new class_2960("centurybridge", "legacy_stem_target"));
    private static final class_5321<class_1792> NO_ITEM =
        class_5321.method_29179(class_7924.field_41197, new class_2960("centurybridge", "legacy_stem_seed"));

    /** StemBlock / AttachedStemBlock (fruit, seed, props): stash fruit and seed, give the first key field nothing */
    public static class_5321<class_2248> legacyStem(StemGrownBlock fruit, Supplier<class_1792> seed) {
        PENDING.set(new LegacyStem(fruit, seed));
        return NO_BLOCK;
    }

    public static class_5321<class_2248> noBlock() {
        return NO_BLOCK;
    }

    public static class_5321<class_1792> noItem() {
        return NO_ITEM;
    }

    /** the 1.20.2 fruit and seed handed to the stem under construction, once */
    public static LegacyStem takePending() {
        LegacyStem s = PENDING.get();
        PENDING.remove();
        return s;
    }

    private CbWorldStems() {
    }
}
