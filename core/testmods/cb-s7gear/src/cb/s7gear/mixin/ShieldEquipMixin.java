package cb.s7gear.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on ShieldItem.getEquipmentSlot: shields go on the head (GearTestMod.equipmentReads) */
@Mixin(class_1819.class)
public abstract class ShieldEquipMixin {

    @Inject(method = "method_7685()Lnet/minecraft/class_1304;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$shieldOnHead(CallbackInfoReturnable<class_1304> cir) {
        cir.setReturnValue(class_1304.field_6169);
    }
}
