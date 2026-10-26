package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11 CameraRenderState.entityPos (class_12075.field_63080): the camera entity's position at the frame's partial
 * tick, which 1.21.11's GameRenderer extracted with the camera state; 26.1 dropped the field. Restored here, filled
 * where 26.1 extracts the camera state (CbWallCameraEntityPosReadsMixin).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = CameraRenderState.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCameraEntityPosMixin implements CbWallLegacyCalls.CameraStateLegacy {

    /** 1.21.11 CameraRenderState.entityPos */
    public Vec3 entityPos;

    @Override
    public void cb$setEntityPos(Vec3 pos) {
        this.entityPos = pos;
    }
}
