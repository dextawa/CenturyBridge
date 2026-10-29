package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on EquipmentDispenseItemBehavior.dispenseEquipment, routed by the layer for saddles */
@Mixin(net.minecraft.class_10205.class)
public abstract class DispenseSeenMixin {
    @Inject(method = "method_64106", at = @At("HEAD"), remap = false)
    private static void cbs215n$dispense(net.minecraft.class_2342 src, net.minecraft.class_1799 s, CallbackInfoReturnable<Boolean> cir) {
        Seen.dispense++;
    }
}
