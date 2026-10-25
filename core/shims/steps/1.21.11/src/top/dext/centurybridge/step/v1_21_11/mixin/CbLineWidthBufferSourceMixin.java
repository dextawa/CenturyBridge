package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_1921;
import net.minecraft.class_287;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbLineWidth;
import top.dext.centurybridge.step.v1_21_11.rt.CbLineWidthTarget;

/**
 * MultiBufferSource.BufferSource.getBuffer(renderType): tells the BufferBuilder it hands out the line width 1.21.10
 * drew that render type's lines at (rt/CbLineWidth; none for render types no 1.21.10 line shard stands behind).
 */
@Mixin(class_4597.class_4598.class)
public abstract class CbLineWidthBufferSourceMixin {
    @Inject(method = "method_73477", at = @At("RETURN"))
    private void cb$lineWidth(class_1921 type, CallbackInfoReturnable<class_4588> cir) {
        if (cir.getReturnValue() instanceof class_287 b) {
            ((CbLineWidthTarget) b).cb$lineWidth(CbLineWidth.width(type));
        }
    }
}
