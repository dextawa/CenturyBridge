package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_243;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbCullFrustum;

/**
 * 1.21.9's LevelRenderer.prepareCullFrustum answers the frustum renderLevel culls the frame with. When a 1.21.8 call
 * set a frustum for this frame (CbCullFrustum), the frame culls with that one instead, as 1.21.8's frame culled with
 * the field the call wrote -- except while the debug-captured frustum is frozen, which 1.21.8 also preferred. A frustum
 * captured in this frame is the one the frame culls with. The slot is cleared at the end of renderLevel.
 */
@Mixin(class_761.class)
public abstract class CbCullFrustumMixin {

    @Shadow
    private class_4604 field_4056;

    @Shadow
    private boolean field_62945;

    @Unique
    private boolean cb$capturing;

    @Inject(method = "method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;", at = @At("HEAD"))
    private void cb$capturing(Matrix4f frustumMatrix, Matrix4f projection, class_243 cameraPos, CallbackInfoReturnable<class_4604> cir) {
        this.cb$capturing = this.field_62945;
    }

    @Inject(method = "method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;", at = @At("RETURN"),
        cancellable = true)
    private void cb$outsideFrustum(Matrix4f frustumMatrix, Matrix4f projection, class_243 cameraPos, CallbackInfoReturnable<class_4604> cir) {
        class_4604 set = CbCullFrustum.cbSlot();
        if (set == null) {
            return;
        }
        if (this.cb$capturing) {
            this.field_4056 = set;
        } else if (this.field_4056 != null) {
            return;
        }
        cir.setReturnValue(set);
    }

    @Inject(method = "method_22710", at = @At("RETURN"))
    private void cb$endFrame(CallbackInfo ci) {
        CbCullFrustum.cbEndFrame();
    }
}
