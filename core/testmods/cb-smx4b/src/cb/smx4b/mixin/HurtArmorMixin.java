package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.4 Inventory.hurtArmor(source, damage, slots): counted, slot count noted */
@Mixin(net.minecraft.class_1661.class)
public abstract class HurtArmorMixin {
    @Inject(method = "method_7375(Lnet/minecraft/class_1282;F[I)V", at = @At("HEAD"), remap = false)
    private void cbsmx4b$hurt(net.minecraft.class_1282 source, float damage, int[] slots, CallbackInfo ci) {
        Driver.hurtArmorAsked++;
        Driver.hurtArmorSlots = slots.length;
    }
}
