package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on Equippable.equipOnTarget, which the 1.21.5 layer routes for saddles */
@Mixin(net.minecraft.class_10192.class)
public abstract class EquipSeenMixin {
    @Inject(method = "method_66700", at = @At("HEAD"), remap = false)
    private void cbs215n$equip(net.minecraft.class_1657 p, net.minecraft.class_1309 t, net.minecraft.class_1799 s,
                               CallbackInfoReturnable<net.minecraft.class_1269> cir) {
        Seen.equip++;
    }
}
