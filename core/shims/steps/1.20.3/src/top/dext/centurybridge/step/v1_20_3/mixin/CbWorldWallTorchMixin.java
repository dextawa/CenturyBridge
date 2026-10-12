package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_2555;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldTorchAccess;

/** WallTorchBlock emits its flame in its own animateTick: the 1.20.2 flame a constructor gave it, as CbWorldTorchMixin */
@Mixin(class_2555.class)
public abstract class CbWorldWallTorchMixin {

    @ModifyArg(method = "method_9496", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1937;method_8406(Lnet/minecraft/class_2394;DDDDDD)V", ordinal = 1), index = 0)
    private class_2394 cb$legacyFlameParticle(class_2394 flame) {
        return ((CbWorldTorchAccess) this).cb$flame();
    }
}
