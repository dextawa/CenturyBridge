package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.11 LeashFeatureRenderer.addVertexPair (class_11688.method_73011, Matrix4f): 26.1 takes a Matrix4fc (mixinTargets narrow) */
@Mixin(net.minecraft.class_11688.class)
public abstract class LeashVertexMixin {
    @Inject(method = "method_73011(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FFFFFFIZLnet/minecraft/class_10017$class_10018;)V", at = @At("HEAD"), remap = false)
    private static void cbswallc$leash(net.minecraft.class_4588 buffer, org.joml.Matrix4f pose, float a, float b, float c, float d, float e, float f,
            int i, boolean z, net.minecraft.class_10017.class_10018 leash, CallbackInfo ci) {
        if (pose != null) SwallClient.hit("leash.addVertexPair");
    }
}
