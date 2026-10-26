package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 Projectile.deflect(deflection, target, owner, fromAttack), counted */
@Mixin(net.minecraft.class_1676.class)
public abstract class NativeDeflectMixin {
    @Inject(method = "method_59859(Lnet/minecraft/class_9109;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Z)V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$deflect(net.minecraft.class_9109 deflection, net.minecraft.class_1297 target, net.minecraft.class_1297 owner,
                                  boolean fromAttack, CallbackInfo ci) {
        cb.smx4bn.Driver.deflect++;
    }
}
