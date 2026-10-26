package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * A 1.21.11 subclass overriding WeatherEffectRenderer.render(bufferSource, cameraPos, state) (restored by
 * CbWallWeatherRenderMixin) is called in place of 26.1's render(cameraPos, state), with the game's buffer source; a
 * renderer without such an override runs 26.1's body untouched. Default priority: injector into a 26.1 method.
 */
@Environment(EnvType.CLIENT)
@Mixin(WeatherEffectRenderer.class)
public abstract class CbWallWeatherRenderReadsMixin {

    @Inject(method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(Vec3 cameraPos, WeatherRenderState state, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, WeatherEffectRenderer.class, "render", MultiBufferSource.class, Vec3.class, WeatherRenderState.class)) {
            ci.cancel();
            ((CbWallLegacyCalls.WeatherLegacy) (Object) this).render(Minecraft.getInstance().renderBuffers().bufferSource(), cameraPos, state);
        }
    }
}
