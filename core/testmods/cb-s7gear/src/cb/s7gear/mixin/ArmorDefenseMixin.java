package cb.s7gear.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1802;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on ArmorItem.getDefense(): an iron chestplate defends 100 (GearTestMod.mobArmorReads) */
@Mixin(class_1738.class)
public abstract class ArmorDefenseMixin {

    @Inject(method = "method_7687()I", at = @At("RETURN"), cancellable = true)
    private void cbs7gear$ironHundred(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == class_1802.field_8523) {
            cir.setReturnValue(100);
        }
    }
}
