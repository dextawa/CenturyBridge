package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's LivingEntity.broadcastBreakEvent(hand) (restored by CbRestLivingEntityMixin): 1.20.4 broke a held
 * item through it; 1.20.5's ItemStack.hurtAndBreak names the slot instead (broadcastBreakEvent(slot)). With a converted
 * mod mixing the restored member, a hand slot's break event goes through it; its body broadcasts the slot's event, so
 * this method then runs as 1.20.5 has it. Unmixed, and for armor slots, it runs as 1.20.5 has it. At ROUTE_PRIORITY so
 * this cancellable HEAD callback comes before a native mod's on the same method (which then fires once, inside).
 */
@Mixin(value = net.minecraft.class_1309.class, priority = CbRoutes.ROUTE_PRIORITY)
public abstract class CbRouteBreakMixin {

    @Inject(method = "method_20235", at = @At("HEAD"), cancellable = true)
    private void cbRoute$breakEvent(net.minecraft.class_1304 slot, CallbackInfo ci) {
        net.minecraft.class_1268 hand = slot == net.minecraft.class_1304.field_6173 ? net.minecraft.class_1268.field_5808
            : slot == net.minecraft.class_1304.field_6171 ? net.minecraft.class_1268.field_5810 : null;
        if (hand != null && CbRoutes.route(CbRoutes.LIVING_BROADCAST_BREAK_HAND, this)) {
            boolean entered = CbRoutes.enter(this);
            try {
                ((CbRoutes.BreakHand) (Object) this).method_20236(hand);
            } finally {
                if (entered) {
                    CbRoutes.exit(this);
                }
            }
            ci.cancel();
        }
    }
}
