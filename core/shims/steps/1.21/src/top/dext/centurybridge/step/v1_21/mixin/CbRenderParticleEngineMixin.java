package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbRenderParticles;

/**
 * ParticleEngine.render with 1.20.6 particle types: a 1.20.6 type's particles go into the builder its begin
 * returns (the Tesselator's 1.20.6 builder, which it began), and where 1.21 would build and draw that builder
 * the type's own end(tesselator) runs instead -- 1.20.6's ParticleEngine called end(tesselator) after the
 * particles, and 1.20.6's ends drew there (tesselator.end()) and restored what their begin set.
 */
@Mixin(net.minecraft.class_702.class)
public abstract class CbRenderParticleEngineMixin {

    @Unique
    private CbRenderParticles.CbRenderLegacyParticleType cb$legacyType;

    @Redirect(method = "method_3049", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3999;method_18130(Lnet/minecraft/class_289;Lnet/minecraft/class_1060;)Lnet/minecraft/class_287;"))
    private net.minecraft.class_287 cb$begin(net.minecraft.class_3999 type, net.minecraft.class_289 tesselator, net.minecraft.class_1060 textures) {
        this.cb$legacyType = type instanceof CbRenderParticles.CbRenderLegacyParticleType legacy ? legacy : null;
        return type.method_18130(tesselator, textures);
    }

    @Redirect(method = "method_3049", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_287;method_60794()Lnet/minecraft/class_9801;"))
    private net.minecraft.class_9801 cb$build(net.minecraft.class_287 builder) {
        CbRenderParticles.CbRenderLegacyParticleType legacy = this.cb$legacyType;
        if (legacy != null) {
            this.cb$legacyType = null;
            legacy.method_18131(net.minecraft.class_289.method_1348());
            return null;
        }
        return builder.method_60794();
    }
}
