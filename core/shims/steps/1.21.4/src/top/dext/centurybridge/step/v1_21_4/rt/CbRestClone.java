package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2480;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import net.minecraft.class_8172;

/**
 * 1.21.3's getCloneItemStack(level, pos, state), which 1.21.4 widened with includeData (the pick-block
 * "with data" request) and made protected (the layer's access widener opens it on every declarer again).
 * 1.21.3's method had no such flag and took no block-entity data, except where a block's own body did:
 * ShulkerBoxBlock saved its block entity into the stack (saveToItem) and DecoratedPotBlock answered the
 * block entity's getPotAsItem() (every component); 1.21.4 dropped both, adding data only on request.
 * <p>
 * A 1.21.3-shaped call (callReshapes) asks 1.21.4's method with includeData false ({@link #legacyCall}) and
 * marks the call as 1.21.3's for its duration ({@link #endLegacyCall}); within it ShulkerBoxBlock and
 * DecoratedPotBlock answer with their 1.21.3 bodies (CbRestCloneBlocksMixin, CbRestClonePotMixin). Vanilla's
 * own calls are unmarked and keep 1.21.4's behaviour. (A call that throws leaves the mark set until the
 * thread's next 1.21.3-shaped call ends.)
 */
public final class CbRestClone {

    private CbRestClone() {
    }

    private static final ThreadLocal<Boolean> LEGACY = new ThreadLocal<>();

    /** the includeData of a 1.21.3 call (none: false), marking the call as 1.21.3's */
    public static boolean legacyCall() {
        LEGACY.set(Boolean.TRUE);
        return false;
    }

    /** the answer of a 1.21.3 call, ending its mark */
    public static class_1799 endLegacyCall(class_1799 stack) {
        LEGACY.remove();
        return stack;
    }

    public static boolean inLegacyCall() {
        return LEGACY.get() != null;
    }

    /** 1.21.3 ShulkerBoxBlock: the block entity saved into Block's stack (its saveToItem, a mod override included) */
    public static void shulkerExtras(class_4970 block, class_4538 level, class_2338 pos, class_1799 stack) {
        if (inLegacyCall() && block instanceof class_2480) {
            level.method_35230(pos, class_2591.field_11896)
                .ifPresent(be -> CbRestBlockEntities.method_38240((class_2586) be, stack, level.method_30349()));
        }
    }

    /** 1.21.3 DecoratedPotBlock: the block entity's getPotAsItem(), or null for 1.21.4's body */
    public static class_1799 potLegacy(class_4538 level, class_2338 pos) {
        if (inLegacyCall() && level.method_8321(pos) instanceof class_8172 pot) {
            return CbRestBlockEntities.method_52578(pot);
        }
        return null;
    }
}
