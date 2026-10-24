package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2394;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinParticles;

/** ParticleEngine.createParticle of a 1.20.4 entity effect particle type: 1.20.5's entity_effect colored by the speeds */
@Environment(EnvType.CLIENT)
@Mixin(class_702.class)
public abstract class CbFinParticleClientMixin {

    @Shadow
    public abstract class_703 method_3056(class_2394 options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

    @Inject(method = "method_3056", at = @At("HEAD"), cancellable = true)
    private void cb$legacyEffect(class_2394 options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                                 CallbackInfoReturnable<class_703> cir) {
        if (options instanceof CbFinParticles.LegacyEffect) {
            cir.setReturnValue(this.method_3056(CbFinParticles.forParticle(options, xSpeed, ySpeed, zSpeed), x, y, z, xSpeed, ySpeed, zSpeed));
        }
    }
}
