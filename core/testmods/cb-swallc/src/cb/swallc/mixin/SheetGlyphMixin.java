package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11 BakedSheetGlyph (class_382): render (method_2025) with every argument captured; renderEffect / renderChar /
 * buildEffect take the package-private EffectInstance / GlyphInstance, so their handlers capture nothing
 */
@Mixin(net.minecraft.class_382.class)
public abstract class SheetGlyphMixin {
    @Inject(method = "method_2025(ZFFFLorg/joml/Matrix4f;Lnet/minecraft/class_4588;IZI)V", at = @At("HEAD"), remap = false)
    private void cbswallc$render(boolean italic, float x, float y, float z, org.joml.Matrix4f pose, net.minecraft.class_4588 buffer, int color,
            boolean bold, int light, CallbackInfo ci) {
        if (pose != null) SwallClient.hit("glyph.render");
    }

    @Inject(method = "method_22944(Lnet/minecraft/class_382$class_328;Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;IZ)V", at = @At("HEAD"), remap = false)
    private void cbswallc$renderEffect(CallbackInfo ci) {
        SwallClient.hit("glyph.renderEffect");
    }

    @Inject(method = "method_65025(Lnet/minecraft/class_382$class_10364;Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;IZ)V", at = @At("HEAD"), remap = false)
    private void cbswallc$renderChar(CallbackInfo ci) {
        SwallClient.hit("glyph.renderChar");
    }

    @Inject(method = "method_65181(Lnet/minecraft/class_382$class_328;FFILnet/minecraft/class_4588;ILorg/joml/Matrix4f;)V", at = @At("HEAD"), remap = false)
    private void cbswallc$buildEffect(CallbackInfo ci) {
        SwallClient.hit("glyph.buildEffect");
    }
}
