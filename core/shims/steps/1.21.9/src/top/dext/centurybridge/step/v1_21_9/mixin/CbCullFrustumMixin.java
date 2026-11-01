package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_4604;
import net.minecraft.class_761;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbCullFrustum;

/**
 * 1.21.9's LevelRenderer.prepareCullFrustum answers the frustum renderLevel culls the frame with: the frozen
 * debug-captured frustum when one is held and no capture is pending, otherwise one it builds; a pending capture then
 * keeps the answer as the captured frustum. When a 1.21.8 call set a frustum for this frame (CbCullFrustum), the frame
 * culls with that one instead of the built one, as 1.21.8's frame culled with the field the call wrote; the frozen
 * captured frustum still wins, as it did on 1.21.8, and a pending capture captures the call's frustum. The slot is
 * cleared at the end of renderLevel.
 * <p>Climb-safe: no @Shadow. The answer is replaced in prepareCullFrustum's own local just before 1.21.9 reads the
 * capture flag, so 1.21.9's capture code keeps it; the frozen case is told apart by the public getCapturedFrustum()
 * (the local is then that very frustum).
 */
@Mixin(class_761.class)
public abstract class CbCullFrustumMixin {

    @ModifyVariable(method = "method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;",
        at = @At(value = "FIELD", target = "Lnet/minecraft/class_761;field_62945:Z", opcode = Opcodes.GETFIELD, ordinal = 1), ordinal = 0)
    private class_4604 cb$outsideFrustum(class_4604 frustum) {
        class_4604 set = CbCullFrustum.cbSlot();
        if (set == null || frustum == ((class_761) (Object) this).method_62222()) {
            return frustum;
        }
        return set;
    }

    @Inject(method = "method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;", at = @At("RETURN"))
    private void cb$frameFrustum(CallbackInfoReturnable<class_4604> cir) {
        CbCullFrustum.cbFrame(cir.getReturnValue());
    }

    @Inject(method = "method_22710", at = @At("RETURN"))
    private void cb$endFrame(CallbackInfo ci) {
        CbCullFrustum.cbEndFrame();
    }
}
