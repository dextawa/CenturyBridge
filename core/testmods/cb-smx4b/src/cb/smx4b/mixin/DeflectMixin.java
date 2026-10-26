package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.4 AbstractArrow.deflect(): counted; cancelled while Driver.cancelDeflect is set (the arrow flies on) */
@Mixin(net.minecraft.class_1665.class)
public abstract class DeflectMixin {
    @Inject(method = "method_55052()V", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx4b$deflect(CallbackInfo ci) {
        Driver.deflectAsked++;
        if (Driver.cancelDeflect) {
            ci.cancel();
        }
    }
}
