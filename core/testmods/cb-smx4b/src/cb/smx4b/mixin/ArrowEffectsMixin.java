package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.4 Arrow.setEffectsFromItem(stack): counted, and cancelled while Driver.cancelArrow is set (the arrow keeps no effects) */
@Mixin(net.minecraft.class_1667.class)
public abstract class ArrowEffectsMixin {
    @Inject(method = "method_7459(Lnet/minecraft/class_1799;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx4b$effects(net.minecraft.class_1799 stack, CallbackInfo ci) {
        Driver.arrowAsked++;
        if (Driver.cancelArrow) {
            ci.cancel();
        }
    }
}
