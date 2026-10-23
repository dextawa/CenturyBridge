package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3609;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_4538;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelFlowing;

/**
 * FlowingFluid: a 1.21.1 override of canSpreadTo decides where 1.21.1 asked it (CbLevelFlowing).
 * <ul>
 * <li>spread, downward: 1.21.1 asked canSpreadTo(.., DOWN, below, belowState, belowFluidState, newLiquid's fluid) and
 *     spread down when it said so; 1.21.2 asks canMaybePassThrough, then the below fluid state's canBeReplacedWith and
 *     canHoldSpecificFluid for the new liquid. For a fluid with a 1.21.1 override the first is its answer (with the
 *     new liquid 1.21.1 computed first) and the other two stand aside.</li>
 * <li>getSpread, to the sides: 1.21.1 kept every direction of least slope and spreadToSides spread only where
 *     canSpreadTo said so; 1.21.2 keeps a direction only where the fluid state there canBeReplacedWith the new liquid
 *     -- the same filter, earlier. For a fluid with a 1.21.1 override that filter is its canSpreadTo.</li>
 * </ul>
 * A fluid without one runs 1.21.2's code unchanged. getSlopeDistance is reachable for a 1.21.1 call.
 */
@Mixin(class_3609.class)
public abstract class CbLevelFlowingFluidMixin implements CbLevelFlowing.FlowingAccess {

    @Shadow
    protected abstract int method_15742(class_4538 level, class_2338 pos, int distance, class_2350 from, class_2680 state,
                                        class_3609.class_9900 context);

    @Shadow
    protected abstract class_3610 method_15727(class_3218 level, class_2338 pos, class_2680 state);

    @Shadow
    private boolean method_61813(class_1922 level, class_2338 pos, class_2680 state, class_2350 direction, class_2338 toPos,
                                 class_2680 toState, class_3610 toFluidState) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean method_61815(class_1922 level, class_2338 pos, class_2680 state, class_3611 fluid) {
        throw new AssertionError();
    }

    @Override
    public int cb$slopeDistance(class_4538 level, class_2338 pos, int distance, class_2350 from, class_2680 state,
                                class_3609.class_9900 context) {
        return this.method_15742(level, pos, distance, from, state, context);
    }

    @Redirect(method = "method_15725", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3609;method_61813(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_3610;)Z"))
    private boolean cb$spreadDown(class_3609 self, class_1922 level, class_2338 pos, class_2680 state, class_2350 direction,
                                  class_2338 toPos, class_2680 toState, class_3610 toFluidState) {
        if (this instanceof CbLevelFlowing.LegacyCanSpreadTo legacy) {
            class_3611 fluid = this.method_15727((class_3218) level, toPos, toState).method_15772();
            return legacy.method_15738(level, pos, state, direction, toPos, toState, toFluidState, fluid);
        }
        return this.method_61813(level, pos, state, direction, toPos, toState, toFluidState);
    }

    @Redirect(method = "method_15725", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3610;method_15764(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_3611;Lnet/minecraft/class_2350;)Z"))
    private boolean cb$spreadDownReplaceable(class_3610 toFluidState, class_1922 level, class_2338 toPos, class_3611 fluid,
                                             class_2350 direction) {
        return this instanceof CbLevelFlowing.LegacyCanSpreadTo || toFluidState.method_15764(level, toPos, fluid, direction);
    }

    @Redirect(method = "method_15725", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3609;method_61815(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_3611;)Z"))
    private boolean cb$spreadDownHolds(class_1922 level, class_2338 toPos, class_2680 toState, class_3611 fluid) {
        return this instanceof CbLevelFlowing.LegacyCanSpreadTo || method_61815(level, toPos, toState, fluid);
    }

    @Redirect(method = "method_15726", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3610;method_15764(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_3611;Lnet/minecraft/class_2350;)Z"))
    private boolean cb$spreadSide(class_3610 toFluidState, class_1922 level, class_2338 toPos, class_3611 fluid, class_2350 direction,
                                  class_3218 serverLevel, class_2338 pos, class_2680 state) {
        if (this instanceof CbLevelFlowing.LegacyCanSpreadTo legacy) {
            return legacy.method_15738(level, pos, state, direction, toPos, level.method_8320(toPos), toFluidState, fluid);
        }
        return toFluidState.method_15764(level, toPos, fluid, direction);
    }
}
