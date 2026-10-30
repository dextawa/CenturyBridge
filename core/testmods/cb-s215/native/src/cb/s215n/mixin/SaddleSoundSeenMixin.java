package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on AbstractHorse's equip sound, routed by the layer for 1.21.4 getSaddleSoundEvent overrides */
@Mixin(net.minecraft.class_1496.class)
public abstract class SaddleSoundSeenMixin {
    @Inject(method = "method_66667", at = @At("HEAD"), remap = false)
    private void cbs215n$sound(net.minecraft.class_1304 slot, net.minecraft.class_1799 stack, net.minecraft.class_10192 eq,
                               CallbackInfoReturnable<net.minecraft.class_6880<net.minecraft.class_3414>> cir) {
        Seen.sound++;
    }
}
