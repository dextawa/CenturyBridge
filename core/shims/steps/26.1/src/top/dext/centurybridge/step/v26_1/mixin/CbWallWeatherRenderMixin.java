package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * 1.21.11's WeatherEffectRenderer.render(bufferSource, cameraPos, state) (class_9976.method_62320). 26.1's
 * render(cameraPos, state) draws the same rain and snow columns through its own render pass instead of a buffer
 * source's consumers, so the old method is 26.1's with the buffer source unused. A subclass overriding the old
 * method is called in its place when vanilla renders the weather, with the game's buffer source, and its super call
 * runs 26.1's (rt/CbWallLegacySplit).
 */
@Environment(EnvType.CLIENT)
@Mixin(WeatherEffectRenderer.class)
public abstract class CbWallWeatherRenderMixin {

    @Shadow
    public abstract void render(Vec3 cameraPos, WeatherRenderState state);

    /** 1.21.11 WeatherEffectRenderer.render (class_9976.method_62320) */
    public void render(MultiBufferSource bufferSource, Vec3 cameraPos, WeatherRenderState state) {
        CbWallLegacySplit.enter(this);
        try {
            render(cameraPos, state);
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(Vec3 cameraPos, WeatherRenderState state, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, WeatherEffectRenderer.class, "render", MultiBufferSource.class, Vec3.class, WeatherRenderState.class)) {
            ci.cancel();
            render(Minecraft.getInstance().renderBuffers().bufferSource(), cameraPos, state);
        }
    }
}
