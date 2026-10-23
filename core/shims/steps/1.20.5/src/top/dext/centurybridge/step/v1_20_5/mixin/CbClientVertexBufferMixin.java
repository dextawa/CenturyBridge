package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientRender;

/**
 * 1.20.4's VertexBuffer.drawWithShader set a shader's IViewRotMat uniform (ShaderInstance.INVERSE_VIEW_ROTATION_MATRIX)
 * to RenderSystem's inverse view rotation before applying the shader; 1.20.5 dropped the uniform. A shader that
 * still declares it (a 1.20.4 mod's) gets it set the same way, from the layer's inverse view rotation.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_291.class)
public abstract class CbClientVertexBufferMixin {

    @Inject(method = "method_34431", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5944;method_34586()V"))
    private void cb$inverseViewRotation(org.joml.Matrix4f modelView, org.joml.Matrix4f projection, net.minecraft.class_5944 shader,
                                        CallbackInfo ci) {
        net.minecraft.class_284 uniform = CbClientRender.inverseViewRotationUniform(shader);
        if (uniform != null) {
            uniform.method_39978(CbClientRender.getInverseViewRotationMatrix());
        }
    }
}
