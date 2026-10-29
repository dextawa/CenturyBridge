package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on Pig.canUseSlot, routed by the layer for 1.21.4 isSaddleable overrides */
@Mixin(net.minecraft.class_1452.class)
public abstract class CanUseSeenMixin {
    @Inject(method = "method_56991", at = @At("HEAD"), remap = false)
    private void cbs215n$canUse(net.minecraft.class_1304 slot, CallbackInfoReturnable<Boolean> cir) {
        Seen.canUse++;
    }
}
