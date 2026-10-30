package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on Mob.isSaddled, routed by the layer for 1.21.4 isSaddled overrides */
@Mixin(net.minecraft.class_1308.class)
public abstract class SaddledSeenMixin {
    @Inject(method = "method_66672", at = @At("HEAD"), remap = false)
    private void cbs215n$saddled(CallbackInfoReturnable<Boolean> cir) {
        Seen.saddled++;
    }
}
