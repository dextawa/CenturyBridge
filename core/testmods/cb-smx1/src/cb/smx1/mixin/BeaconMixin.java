package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BeaconBlockEntity.getValidEffectById(int) (method_10934, static), which 1.20.1's load(tag) calls for the primary
 * and secondary effect. While the driver asks for it, the handler answers Luck, as a 1.20.1 mod forcing a beacon
 * effect would.
 */
@Mixin(net.minecraft.class_2580.class)
public abstract class BeaconMixin {
    @Inject(method = "method_10934(I)Lnet/minecraft/class_1291;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cbsmx1$effect(CallbackInfoReturnable<net.minecraft.class_1291> cir) {
        if (Driver.forceLuck) {
            Driver.count("beacon-effect-head");
            cir.setReturnValue(net.minecraft.class_1294.field_5926);
        }
    }
}
