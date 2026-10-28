package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's cancellable HEAD on Entity.changeDimension(ServerLevel) capturing the destination -- the shape of
 * Kilt's EntityInject.handleChangeDimensionEvent (Forge's travel-to-dimension event): refuses the move while
 * Driver.blockDim is set (1.21: s121.json mixinTargets onto changeDimension(DimensionTransition), dest = its level).
 */
@Mixin(net.minecraft.class_1297.class)
public abstract class DimensionMixin {

    @Inject(method = "method_5731(Lnet/minecraft/class_3218;)Lnet/minecraft/class_1297;", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx6$travel(net.minecraft.class_3218 dest, CallbackInfoReturnable<net.minecraft.class_1297> cir) {
        Driver.dimAsked++;
        Driver.dimDest = dest;
        if (Driver.blockDim) {
            cir.setReturnValue(null);
        }
    }
}
