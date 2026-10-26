package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 Arrow.updateColor(), which the constructors taking the ammo stack call */
@Mixin(net.minecraft.class_1667.class)
public abstract class NativeArrowMixin {
    @Inject(method = "method_7462()V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$color(CallbackInfo ci) {
        cb.smx4bn.Driver.arrowColor++;
    }
}
