package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2839;
import net.minecraft.class_2922;
import net.minecraft.class_5485;
import net.minecraft.class_6643;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.facade.GenerationStepCarving;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/**
 * NoiseBasedChunkGenerator.applyCarvers carving 1.21.1's LIQUID step (CbLevelCarving): 1.21.2's body is 1.21.1's for
 * one step; while a LIQUID applyCarvers runs, its mask and carvers are the LIQUID ones. Otherwise untouched.
 */
@Mixin(net.minecraft.class_3754.class)
public abstract class CbLevelCarvingNoiseMixin {

    @Redirect(method = "method_12108", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2839;method_28510()Lnet/minecraft/class_6643;"))
    private class_6643 cb$mask(class_2839 chunk) {
        return CbLevelCarving.liquid() ? CbLevelCarving.method_28510(chunk, GenerationStepCarving.LIQUID) : chunk.method_28510();
    }

    @Redirect(method = "method_12108", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5485;method_30976()Ljava/lang/Iterable;"))
    private Iterable<class_6880<class_2922<?>>> cb$carvers(class_5485 settings) {
        return CbLevelCarving.liquid() ? CbLevelCarving.method_30976(settings, GenerationStepCarving.LIQUID) : settings.method_30976();
    }
}
