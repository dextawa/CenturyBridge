package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 PathfinderMob.isPanicking, counted */
@Mixin(net.minecraft.class_1314.class)
public abstract class NativePanicMixin {
    @Inject(method = "method_52546()Z", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$panicking(CallbackInfoReturnable<Boolean> cir) {
        cb.smx4bn.Driver.panicking++;
    }
}
