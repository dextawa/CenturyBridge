package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientRender;

/**
 * 1.20.4's GameRenderer.renderLevel turned its PoseStack by the camera (x rotation, then y + 180 degrees)
 * and set the inverse of that rotation as RenderSystem's inverse view rotation, which shaders declaring
 * IViewRotMat read. 1.20.5 builds the same rotation as a matrix of its own and keeps no inverse: the
 * layer's inverse view rotation (rt/CbClientRender) is set from it, once per frame, where 1.20.4 set it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbClientGameRendererMixin {

    @Redirect(method = "method_3188(FJ)V", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;rotationXYZ(FFF)Lorg/joml/Matrix4f;"))
    private org.joml.Matrix4f cb$cameraRotation(org.joml.Matrix4f matrix, float x, float y, float z) {
        org.joml.Matrix4f rotation = matrix.rotationXYZ(x, y, z);
        CbClientRender.cameraRotation(rotation);
        return rotation;
    }
}
