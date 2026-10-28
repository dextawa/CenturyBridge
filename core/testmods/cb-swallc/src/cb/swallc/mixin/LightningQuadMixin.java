package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.11 LightningBoltRenderer.quad (class_919.method_23183, Matrix4f): 26.1 takes a Matrix4fc */
@Mixin(net.minecraft.class_919.class)
public abstract class LightningQuadMixin {
    @Inject(method = "method_23183(Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;FFIFFFFFFFZZZZ)V", at = @At("HEAD"), remap = false)
    private static void cbswallc$quad(org.joml.Matrix4f pose, net.minecraft.class_4588 buffer, float a, float b, int c, float d, float e, float f, float g,
            float h, float i, float j, boolean k, boolean l, boolean m, boolean n, CallbackInfo ci) {
        if (pose != null) SwallClient.hit("lightning.quad");
    }
}
