package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own AbstractHorse.getPassengerAttachmentPoint that goes with
 * {@link CbRestAbstractHorseMixin} (its restored 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}): where a
 * 1.20.4 override of getPassengersRidingOffsetY answers another height, 1.20.5's point is moved to it.
 */
@Mixin(net.minecraft.class_1496.class)
public abstract class CbRestAbstractHorseMixinHooks {

    @Inject(method = "method_52533", at = @At("RETURN"), cancellable = true)
    private void cbRest$ridingHeight(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale,
                                     CallbackInfoReturnable<net.minecraft.class_243> cir) {
        CbPrioritySplit.HorseRiding riding = (CbPrioritySplit.HorseRiding) (Object) this;
        float base = riding.cb$v1_20_5$attachmentHeight(dimensions);
        float legacy = riding.cb$v1_20_5$passengersRidingOffsetY(dimensions, scale);
        if (legacy != base) {
            net.minecraft.class_243 r = cir.getReturnValue();
            cir.setReturnValue(new net.minecraft.class_243(r.field_1352, r.field_1351 - base + legacy, r.field_1350));
        }
    }
}
