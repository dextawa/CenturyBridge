package cb.smx6c.mixin;

import cb.smx6c.Counters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.6 mod's HEAD injectors on EnderDragonRenderer's private static death-ray vertex helpers (1.20.6 names and
 * descriptors): vertex01 (centre, alpha), vertex2 / vertex3 / vertex4 (corners A / B / C, height and width). Counts the
 * calls and the arguments 1.20.6 could never pass (alpha outside 0..255, width below 1, height not above 0).
 */
@Mixin(net.minecraft.class_895.class)
public abstract class DragonRaysMixin {

    @Inject(method = "method_23157(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;I)V", at = @At("HEAD"))
    private static void cb$centre(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, int alpha, CallbackInfo ci) {
        Counters.RAY_CENTRE.incrementAndGet();
        if (alpha < 0 || alpha > 255) {
            Counters.RAY_BAD.incrementAndGet();
        }
    }

    @Inject(method = "method_23156(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V", at = @At("HEAD"))
    private static void cb$a(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w, CallbackInfo ci) {
        Counters.RAY_A.incrementAndGet();
        corner(h, w);
    }

    @Inject(method = "method_23158(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V", at = @At("HEAD"))
    private static void cb$b(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w, CallbackInfo ci) {
        Counters.RAY_B.incrementAndGet();
        corner(h, w);
    }

    @Inject(method = "method_23159(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V", at = @At("HEAD"))
    private static void cb$c(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w, CallbackInfo ci) {
        Counters.RAY_C.incrementAndGet();
        corner(h, w);
    }

    private static void corner(float h, float w) {
        if (!(w >= 1.0F) || !(h > 0.0F)) {
            Counters.RAY_BAD.incrementAndGet();
        }
    }
}
