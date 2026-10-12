package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_2400;
import net.minecraft.class_2527;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldBlocks;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldTorchAccess;

/**
 * 1.20.2 torches took any ParticleOptions as their flame; 1.20.3's field holds a SimpleParticleType.
 * A torch a 1.20.2 constructor gave another particle (CbWorldBlocks.torchFlame) keeps it here and
 * emits it where TorchBlock.animateTick emits its flame, as 1.20.2 did.
 */
@Mixin(class_2527.class)
public abstract class CbWorldTorchMixin implements CbWorldTorchAccess {

    @Shadow
    @Final
    protected class_2400 field_22155;

    @Unique
    private class_2394 cb$legacyFlame;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$takeLegacyFlame(class_2400 flame, class_4970.class_2251 properties, CallbackInfo ci) {
        this.cb$legacyFlame = CbWorldBlocks.takePendingTorchFlame();
    }

    /** the second particle animateTick adds is the flame (the first is SMOKE) */
    @ModifyArg(method = "method_9496", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1937;method_8406(Lnet/minecraft/class_2394;DDDDDD)V", ordinal = 1), index = 0)
    private class_2394 cb$legacyFlameParticle(class_2394 flame) {
        return this.cb$legacyFlame != null ? this.cb$legacyFlame : flame;
    }

    @Override
    public class_2394 cb$flame() {
        return this.cb$legacyFlame != null ? this.cb$legacyFlame : this.field_22155;
    }
}
