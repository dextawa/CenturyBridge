package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 LivingEntity.doHurtEquipment(source, damage, slots), which Player.hurtArmor calls */
@Mixin(net.minecraft.class_1309.class)
public abstract class NativeHurtEquipmentMixin {
    @Inject(method = "method_57292(Lnet/minecraft/class_1282;F[Lnet/minecraft/class_1304;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$hurt(net.minecraft.class_1282 source, float damage, net.minecraft.class_1304[] slots, CallbackInfo ci) {
        cb.smx4bn.Driver.hurtEquipment++;
    }
}
