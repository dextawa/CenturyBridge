package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_2675;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbParticleOptions;

/**
 * ClientboundLevelParticlesPacket(options, ..) -- ServerLevel.sendParticles and every mod sending particles build
 * one: a 1.21.8 type that 1.21.9 gave options of their own (CbParticleOptions.Legacy) is sent as those options (the
 * packet encodes the options through their type's stream codec). Any other options pass unchanged.
 */
@Mixin(class_2675.class)
public abstract class CbLevelParticlesPacketMixin {

    @Shadow
    @Final
    @Mutable
    private class_2394 field_12259;

    @Inject(method = "<init>(Lnet/minecraft/class_2394;ZZDDDFFFFI)V", at = @At("RETURN"))
    private void cb$legacyOptions(CallbackInfo ci) {
        this.field_12259 = CbParticleOptions.current(this.field_12259);
    }
}
