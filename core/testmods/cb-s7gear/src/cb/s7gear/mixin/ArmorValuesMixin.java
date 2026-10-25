package cb.s7gear.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.1 mod mixin on ArmorItem's getEnchantmentValue / isValidRepairItem overrides: an iron chestplate is not
 * enchantable and is repaired with diamonds (GearTestMod.overrideReads)
 */
@Mixin(class_1738.class)
public abstract class ArmorValuesMixin {

    @Inject(method = "method_7837()I", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$ironChestUnenchantable(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == class_1802.field_8523) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "method_7878(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;)Z", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$ironChestDiamondRepair(class_1799 stack, class_1799 ingredient, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == class_1802.field_8523 && ingredient.method_31574(class_1802.field_8477)) {
            cir.setReturnValue(true);
        }
    }
}
