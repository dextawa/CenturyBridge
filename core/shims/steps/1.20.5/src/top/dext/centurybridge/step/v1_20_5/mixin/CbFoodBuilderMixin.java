package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFood;

/** FoodProperties.Builder.build(): a builder 1.20.4's meat() was called on built a meat food (rt/CbFood) */
@Mixin(net.minecraft.class_4174.class_4175.class)
public abstract class CbFoodBuilderMixin {

    @Inject(method = "method_19242", at = @At("RETURN"))
    private void cbS4$built(CallbackInfoReturnable<net.minecraft.class_4174> cir) {
        CbFood.built((net.minecraft.class_4174.class_4175) (Object) this, cir.getReturnValue());
    }
}
