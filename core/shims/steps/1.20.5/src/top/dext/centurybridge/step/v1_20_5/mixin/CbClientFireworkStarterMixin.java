package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientParticles;

/**
 * FireworkParticles.Starter built from a 1.20.4 tag without explosions (rt/CbClientParticles.NO_EXPLOSIONS):
 * 1.20.4 made it an invisible particle that lives 8 ticks and is removed without a burst, a sound or a
 * twinkle. 1.20.5's constructor would refuse the empty list; for that list only it passes, the lifetime is
 * 1.20.4's 8, and the tick is 1.20.4's for a starter without explosions. Every other starter is 1.20.5's.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_677.class_681.class)
public abstract class CbClientFireworkStarterMixin extends net.minecraft.class_3998 {

    @Shadow
    private int field_3808;

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_9283> field_3806;

    private CbClientFireworkStarterMixin() {
        super(null, 0.0D, 0.0D, 0.0D);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean cb$legacyNoExplosions(java.util.List<?> explosions) {
        return explosions != CbClientParticles.NO_EXPLOSIONS && explosions.isEmpty();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$legacyLifetime(CallbackInfo ci) {
        if (this.field_3806 == CbClientParticles.NO_EXPLOSIONS) {
            this.field_3847 = 8;
        }
    }

    @Inject(method = "method_3070", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTick(CallbackInfo ci) {
        if (this.field_3806 == CbClientParticles.NO_EXPLOSIONS) {
            ci.cancel();
            if (++this.field_3808 > this.field_3847) {
                this.method_3085();
            }
        }
    }
}
