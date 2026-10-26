package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.4 Fireball.getItemRaw(): counted */
@Mixin(net.minecraft.class_3855.class)
public abstract class FireballRawMixin {
    @Inject(method = "method_16938()Lnet/minecraft/class_1799;", at = @At("RETURN"), remap = false)
    private void cbsmx4b$raw(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        Driver.fireAsked++;
    }
}
