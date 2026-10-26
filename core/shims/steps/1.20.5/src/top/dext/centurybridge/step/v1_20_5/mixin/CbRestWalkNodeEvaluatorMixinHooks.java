package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPath;

/**
 * The injector into 1.20.5's own WalkNodeEvaluator.canStartAt / getStartNode that goes with
 * {@link CbRestWalkNodeEvaluatorMixin} (its restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}, so
 * a mod's 1.20.4 injectors can anchor inside them): a class overriding 1.20.4's getBlockPathType(mob, pos) is asked
 * again.
 */
@Mixin(net.minecraft.class_14.class)
public abstract class CbRestWalkNodeEvaluatorMixinHooks {

    /** canStartAt / getStartNode asked 1.20.4's getBlockPathType(mob, pos): a class overriding it is asked again */
    @Redirect(method = {"method_47893", "method_43415"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_14;method_31932(III)Lnet/minecraft/class_7;"))
    private net.minecraft.class_7 cbRest$startType(net.minecraft.class_14 self, int x, int y, int z) {
        return CbRestPath.startType(self, x, y, z);
    }
}
