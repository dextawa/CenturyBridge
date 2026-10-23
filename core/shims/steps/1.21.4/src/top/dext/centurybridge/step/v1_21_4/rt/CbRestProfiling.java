package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import net.minecraft.class_1657;
import net.minecraft.class_1915;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_3195;
import net.minecraft.class_5321;
import net.minecraft.class_6611;
import net.minecraft.class_6613;
import net.minecraft.class_6880;

/**
 * 1.21.3 profiling and merchant contracts 1.21.4 widened.
 * <ul>
 * <li>JvmProfiler.onStructureGenerate(chunk, level, structure): new and abstract in 1.21.4 (abstractFills).
 *     1.21.3 reported no structure generation, so a 1.21.3 profiler records nothing for it: a duration
 *     whose finish does nothing.</li>
 * <li>ProfiledDuration.finish(): 1.21.4's finish(success). A 1.21.3 implementation (class: overrideHooks;
 *     lambda: lambdaAdapters) is finished regardless of the flag, as 1.21.3 had none; a 1.21.3 call finishes
 *     it as a success (1.21.4's callers pass false only for a structure that failed to generate, which
 *     1.21.3 never reported).</li>
 * <li>JfrStatsResult(.., chunk generation stats..): 1.21.4 adds the structure generation stats, which
 *     1.21.3's profiler did not collect (none).</li>
 * <li>Merchant.stillValid(player): new and abstract in 1.21.4, which asks it from MerchantMenu.stillValid.
 *     1.21.3's MerchantMenu.stillValid asked whether the merchant's trading player is that player, so a
 *     1.21.3 merchant answers exactly that.</li>
 * </ul>
 */
public final class CbRestProfiling {

    private CbRestProfiling() {
    }

    public static class_6613 onStructureGenerate(class_6611 profiler, class_1923 chunk, class_5321<class_1937> level, class_6880<class_3195> structure) {
        return success -> {
        };
    }

    public interface LegacyDuration {
        void finish();
    }

    /** 1.21.4's finish(success) of a class implementing 1.21.3's finish() */
    public static void finish(LegacyDuration self, boolean success) {
        self.finish();
    }

    public static class_6613 duration(LegacyDuration legacy) {
        return success -> legacy.finish();
    }

    public static boolean success() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    public static List noStructureGenerationStats() {
        return List.of();
    }

    public static boolean stillValid(class_1915 merchant, class_1657 player) {
        return merchant.method_8257() == player;
    }
}
