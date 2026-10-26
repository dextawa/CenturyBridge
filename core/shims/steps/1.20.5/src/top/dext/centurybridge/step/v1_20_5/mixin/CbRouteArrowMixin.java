package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's Arrow.setEffectsFromItem(stack) (restored by CbEntityArrowMixin): 1.20.4 made an arrow without
 * effects and then applied the ammo stack through it (ArrowItem.createArrow, ProjectileUtil.getMobArrow, the tipped
 * arrow dispenser behaviour); 1.20.5 hands the ammo stack to the constructor, whose pickup stack keeps its potion
 * contents. With a converted mod mixing the restored member, those constructors end by clearing the contents and
 * applying the stack through it, as 1.20.4 did; unmixed they run as 1.20.5 has them.
 */
@Mixin(net.minecraft.class_1667.class)
public abstract class CbRouteArrowMixin {

    @Shadow
    private void method_57316(net.minecraft.class_1844 contents) {
        throw new AssertionError();
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;)V", at = @At("RETURN"))
    private void cbRoute$shotFrom(net.minecraft.class_1937 level, net.minecraft.class_1309 owner, net.minecraft.class_1799 stack, CallbackInfo ci) {
        this.cbRoute$apply(stack);
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1937;DDDLnet/minecraft/class_1799;)V", at = @At("RETURN"))
    private void cbRoute$placedAt(net.minecraft.class_1937 level, double x, double y, double z, net.minecraft.class_1799 stack, CallbackInfo ci) {
        this.cbRoute$apply(stack);
    }

    private void cbRoute$apply(net.minecraft.class_1799 stack) {
        if (CbRoutes.route(CbRoutes.ARROW_SET_EFFECTS_FROM_ITEM, this)) {
            this.method_57316(net.minecraft.class_1844.field_49274);
            ((CbRoutes.ArrowEffectsFromItem) (Object) this).method_7459(stack);
        }
    }
}
