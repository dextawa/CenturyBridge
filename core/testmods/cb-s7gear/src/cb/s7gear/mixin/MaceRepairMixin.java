package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_9362;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** a 1.21.1 mod mixin redirecting MaceItem.isValidRepairItem's breeze-rod test: diamonds repair it too (GearTestMod.overrideReads) */
@Mixin(class_9362.class)
public abstract class MaceRepairMixin {

    @Redirect(method = "method_7878(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_31574(Lnet/minecraft/class_1792;)Z"))
    private boolean cbs7gear$diamondsToo(class_1799 ingredient, class_1792 item) {
        return ingredient.method_31574(item) || ingredient.method_31574(class_1802.field_8477);
    }
}
