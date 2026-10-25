package cb.s7gear.mixin;

import net.minecraft.class_1835;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on TridentItem.getEnchantmentValue: tridents are not enchantable (GearTestMod.overrideReads) */
@Mixin(class_1835.class)
public abstract class TridentValueMixin {

    @Inject(method = "method_7837()I", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$noTridentEnchanting(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
