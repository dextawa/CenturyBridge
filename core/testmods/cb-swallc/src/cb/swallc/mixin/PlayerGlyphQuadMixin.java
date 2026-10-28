package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.11 PlayerGlyphProvider$Instance.renderQuad (class_11879$class_11880.method_74042, Matrix4f; the class is package-private) */
@Mixin(targets = "net.minecraft.class_11879$class_11880")
public abstract class PlayerGlyphQuadMixin {
    @Inject(method = "method_74042(Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;IFFFFFIFFIIII)V", at = @At("HEAD"), remap = false)
    private static void cbswallc$quad(org.joml.Matrix4f pose, net.minecraft.class_4588 buffer, int a, float b, float c, float d, float e, float f, int g,
            float h, float i, int j, int k, int l, int m, CallbackInfo ci) {
        if (pose != null) SwallClient.hit("playerGlyph.renderQuad");
    }
}
