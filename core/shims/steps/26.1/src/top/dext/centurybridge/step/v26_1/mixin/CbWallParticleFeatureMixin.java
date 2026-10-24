package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallParticles;

/** records the translucency of the particle pass running (rt/CbWallParticles) */
@Environment(EnvType.CLIENT)
@Mixin(ParticleFeatureRenderer.class)
public abstract class CbWallParticleFeatureMixin {

    @Inject(method = "render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Z)V", at = @At("HEAD"))
    private void cb$passStarted(SubmitNodeCollection collection, boolean translucent, CallbackInfo ci) {
        CbWallParticles.passStarted(translucent);
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Z)V", at = @At("RETURN"))
    private void cb$passEnded(SubmitNodeCollection collection, boolean translucent, CallbackInfo ci) {
        CbWallParticles.passEnded();
    }
}
