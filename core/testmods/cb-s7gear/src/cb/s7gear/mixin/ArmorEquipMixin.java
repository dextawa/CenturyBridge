package cb.s7gear.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1738;
import net.minecraft.class_1802;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.1 mod mixin on ArmorItem.getEquipmentSlot / getEquipSound: a leather helmet goes to the chest, golden boots
 * sound like an elytra (GearTestMod.equipmentReads)
 */
@Mixin(class_1738.class)
public abstract class ArmorEquipMixin {

    @Inject(method = "method_7685()Lnet/minecraft/class_1304;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$leatherHelmetOnChest(CallbackInfoReturnable<class_1304> cir) {
        if ((Object) this == class_1802.field_8267) {
            cir.setReturnValue(class_1304.field_6174);
        }
    }

    @Inject(method = "method_31570()Lnet/minecraft/class_6880;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$goldenBootsSound(CallbackInfoReturnable<class_6880<class_3414>> cir) {
        if ((Object) this == class_1802.field_8753) {
            cir.setReturnValue(class_3417.field_14966);
        }
    }
}
