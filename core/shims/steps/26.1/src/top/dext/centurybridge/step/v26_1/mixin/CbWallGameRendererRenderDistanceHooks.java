package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallRenderDistance;

/**
 * 1.21.11 GameRenderer.renderDistance (class_757.field_4025) written by a mod. 1.21.11's render(DeltaTracker) set it to
 * options.getEffectiveRenderDistance() * 16 at the start of every frame, and getRenderDistance() / getDepthFar() read it,
 * so a write held until the next frame's reset. 26.1 keeps no such field; the frame's far plane is computed at the start
 * of GameRenderer.update by Camera.update (javap 26.1), which takes it from the restored getDepthFar()
 * (mixin/CbWallCameraDepthFarMixin) and so from the restored getRenderDistance() (mixin/CbWallGameRendererMixin).
 * The written value is kept here and reset at the head of update, 26.1's frame start, as 1.21.11 reset it at render's
 * start; getRenderDistance() answers it while set. A write made after this frame's Camera.update reaches readers of
 * getRenderDistance() at once and the far plane from the next frame (1.21.11 built its projection later in the frame).
 */
@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class CbWallGameRendererRenderDistanceHooks implements CbWallRenderDistance {

    @Unique
    private float cb$renderDistance = Float.NaN;

    @Override
    public float cb$writtenRenderDistance() {
        return this.cb$renderDistance;
    }

    @Override
    public void cb$writeRenderDistance(float v) {
        this.cb$renderDistance = v;
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void cb$frameResetRenderDistance(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        this.cb$renderDistance = Float.NaN;
    }
}
