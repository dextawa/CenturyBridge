package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.11 ShadowFeatureRenderer.shadowVertex (class_11690.method_73016, Matrix4f): 26.1 takes a Matrix4fc */
@Mixin(net.minecraft.class_11690.class)
public abstract class ShadowVertexMixin {
    @Inject(method = "method_73016(Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;IFFFFF)V", at = @At("HEAD"), remap = false)
    private static void cbswallc$shadow(org.joml.Matrix4f pose, net.minecraft.class_4588 buffer, int color, float a, float b, float c, float d, float e,
            CallbackInfo ci) {
        if (pose != null) SwallClient.hit("shadow.shadowVertex");
    }
}
