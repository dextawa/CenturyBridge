package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 Piglin.playAngrySound(), which calls Entity.makeSound */
@Mixin(net.minecraft.class_4836.class)
public abstract class NativeMakeSoundMixin {
    @Inject(method = "method_30238()V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$angry(CallbackInfo ci) {
        cb.smx4bn.Driver.angrySound++;
    }
}
