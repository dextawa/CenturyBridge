package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 26.1's Camera.extractRenderState(state, partialTick) also fills the restored 1.21.11 CameraRenderState.entityPos
 * (CbWallCameraEntityPosMixin) as 1.21.11's GameRenderer did with the camera state: the camera entity's
 * getPosition(partialTick). Default priority: injector into a 26.1 method.
 */
@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CbWallCameraEntityPosReadsMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/renderer/state/level/CameraRenderState;F)V", at = @At("TAIL"))
    private void cb$entityPos(CameraRenderState state, float partialTick, CallbackInfo ci) {
        Entity entity = ((Camera) (Object) this).entity();
        if (entity != null) {
            ((CbWallLegacyCalls.CameraStateLegacy) state).cb$setEntityPos(entity.getPosition(partialTick));
        }
    }
}
