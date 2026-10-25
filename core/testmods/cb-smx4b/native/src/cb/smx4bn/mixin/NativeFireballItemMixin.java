package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 Fireball.getItem() */
@Mixin(net.minecraft.class_3855.class)
public abstract class NativeFireballItemMixin {
    @Inject(method = "method_7495()Lnet/minecraft/class_1799;", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$item(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        cb.smx4bn.Driver.fireItem++;
    }
}
