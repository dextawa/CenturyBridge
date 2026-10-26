package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * A 1.21.11 subclass overriding ScreenEffectRenderer.renderScreenEffect(sleeping, partialTick, collector) (restored by
 * CbWallScreenEffectMixin) is called in place of 26.1's five-argument shape, its super call seeing the values vanilla
 * passed; a renderer without such an override runs 26.1's body untouched. Default priority: injector into a 26.1
 * method.
 */
@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class CbWallScreenEffectReadsMixin {

    @Inject(method = "renderScreenEffect(ZZFLnet/minecraft/client/renderer/SubmitNodeCollector;Z)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacy(boolean firstPerson, boolean sleeping, float partialTick, SubmitNodeCollector collector, boolean hideGui, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ScreenEffectRenderer.class, "renderScreenEffect", boolean.class, float.class, SubmitNodeCollector.class)) {
            ci.cancel();
            ((CbWallLegacyCalls.ScreenEffectLegacy) (Object) this).cb$renderLegacyFramed(firstPerson, sleeping, partialTick, collector, hideGui);
        }
    }
}
