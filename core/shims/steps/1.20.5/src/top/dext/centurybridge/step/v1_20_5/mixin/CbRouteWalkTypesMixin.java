package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPath;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's WalkNodeEvaluator.getBlockPathTypes(level, x, y, z, types, current, mobPos) and
 * evaluateBlockPathType(level, pos, type) (restored by CbRestWalkNodeEvaluatorMixin): 1.20.4's
 * getBlockPathType(level, x, y, z, mob) collected the mob box's types through the first, which evaluated each cell
 * through the second; 1.20.5's getPathTypeOfMob collects them through getPathTypeWithinMobBB, the two bodies folded
 * into one (the same types: doors as the evaluator may pass them, rails only on rails). With a converted mod mixing
 * either restored member, getPathTypeWithinMobBB answers through them (CbRestPath.withinMobBB, as for a class
 * overriding them); unmixed it runs as 1.20.5 has it. Subclasses (AmphibiousNodeEvaluator) inherit it, as 1.20.4's.
 */
@Mixin(value = net.minecraft.class_14.class, priority = CbRoutes.ROUTE_PRIORITY)
public abstract class CbRouteWalkTypesMixin {

    @Inject(method = "method_57090", at = @At("HEAD"), cancellable = true)
    private void cbRoute$withinMobBB(net.minecraft.class_9316 context, int x, int y, int z,
                                     CallbackInfoReturnable<java.util.Set<net.minecraft.class_7>> cir) {
        if (CbRoutes.route(CbRoutes.WALK_BOX_TYPES, this) || CbRoutes.route(CbRoutes.WALK_EVALUATE, this)) {
            cir.setReturnValue(CbRestPath.withinMobBB((CbRestPath.LegacyBoxTypes) (Object) this, context, x, y, z));
        }
    }
}
