package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallDepthFar;

/**
 * 1.21.11's far plane came from GameRenderer.getDepthFar() (the main projection, GameRenderer.getProjectionMatrix,
 * and LevelRenderer's sky pass read it). 26.1 moved the same formula -- the larger of four render distances and the
 * cloud range -- into Camera.update, which stores it in depthFar (the projection's far distance, the culling
 * frustum's, the camera render state's). Camera.update takes it from the GameRenderer.getDepthFar() the layer
 * restores (mixin/CbWallGameRendererMixin) instead, so a 1.21.11 override of it or a mod's injector on it (or on
 * getRenderDistance, which it reads) sets the far plane again. Unchanged, the restored method computes 26.1's value.
 */
@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CbWallCameraDepthFarMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private float depthFar;

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Camera;depthFar:F",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void cb$legacyDepthFar(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.gameRenderer instanceof CbWallDepthFar renderer) {
            this.depthFar = renderer.getDepthFar();
        }
    }
}
