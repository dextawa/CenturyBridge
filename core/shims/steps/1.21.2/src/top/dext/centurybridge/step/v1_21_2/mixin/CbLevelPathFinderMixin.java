package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Set;
import net.minecraft.class_11;
import net.minecraft.class_13;
import net.minecraft.class_1308;
import net.minecraft.class_1950;
import net.minecraft.class_2338;
import net.minecraft.class_3695;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelMisc;

/**
 * PathFinder: 1.21.1's findPath profiled the search on the searched region's getProfiler(), 1.21.2's private search
 * on the thread's profiler. The region is noted while findPath runs, and a 1.21.1 region's getProfiler override
 * answers the search's profiler (CbLevelMisc).
 */
@Mixin(class_13.class)
public abstract class CbLevelPathFinderMixin {

    @Inject(method = "method_52", at = @At("HEAD"))
    private void cb$searchStart(class_1950 region, class_1308 mob, Set<class_2338> targets, float maxRange, int reach,
                                float maxVisited, CallbackInfoReturnable<class_11> cir) {
        CbLevelMisc.searchedRegion(region);
    }

    @Inject(method = "method_52", at = @At("RETURN"))
    private void cb$searchDone(class_1950 region, class_1308 mob, Set<class_2338> targets, float maxRange, int reach,
                               float maxVisited, CallbackInfoReturnable<class_11> cir) {
        CbLevelMisc.searchedRegion(null);
    }

    @Redirect(method = "method_54", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_10209;method_64146()Lnet/minecraft/class_3695;"))
    private class_3695 cb$searchProfiler() {
        return CbLevelMisc.searchProfiler(net.minecraft.class_10209.method_64146());
    }
}
