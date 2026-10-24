package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderShaders;

/** Hands each compiled program the key it was compiled for: its 1.21.1 name and vertex format (CbRenderShaderMixin). */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10151.class)
public abstract class CbRenderShaderManagerMixin {

    @Inject(method = "method_62947(Lnet/minecraft/class_10156;)Lnet/minecraft/class_5944;", at = @At("RETURN"))
    private void cb$program(net.minecraft.class_10156 key, CallbackInfoReturnable<net.minecraft.class_5944> cir) {
        if (cir.getReturnValue() != null) {
            ((CbRenderShaders.Program) cir.getReturnValue()).cb$compiledFor(key);
        }
    }

    @Inject(method = "method_64062(Lnet/minecraft/class_10156;)Lnet/minecraft/class_5944;", at = @At("RETURN"))
    private void cb$programForLoading(net.minecraft.class_10156 key, CallbackInfoReturnable<net.minecraft.class_5944> cir) {
        if (cir.getReturnValue() != null) {
            ((CbRenderShaders.Program) cir.getReturnValue()).cb$compiledFor(key);
        }
    }
}
