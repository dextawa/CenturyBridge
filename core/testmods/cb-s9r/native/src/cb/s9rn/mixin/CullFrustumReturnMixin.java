package cb.s9rn.mixin;

import cb.s9rn.Driver;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a native 1.21.9 mixin at RETURN of LevelRenderer.prepareCullFrustum, where the layer keeps the frame's frustum for
 * the 1.21.8 cullingFrustum field (CbCullFrustumMixin): it must still run, and see the frustum the frame culls with
 */
@Mixin(class_761.class)
public abstract class CullFrustumReturnMixin {
    @Inject(method = "method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;", at = @At("RETURN"))
    private void cbs9rn$frustum(CallbackInfoReturnable<class_4604> cir) {
        Driver.frustumReturned(cir.getReturnValue());
    }
}
