package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1802;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on Item.getEnchantmentValue(): a stick is enchantable at 30 (GearTestMod.restoredMixins) */
@Mixin(class_1792.class)
public abstract class EnchantValueMixin {

    @Inject(method = "method_7837()I", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$stickValue(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == class_1802.field_8600) {
            cir.setReturnValue(30);
        }
    }
}
