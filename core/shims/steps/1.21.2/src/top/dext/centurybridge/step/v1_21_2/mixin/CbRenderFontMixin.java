package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 Font.drawInBatch overloads 1.21.2 reshaped:
 * - drawInBatch(String, ..., backgroundColor, light, bidirectional): 1.21.1 shaped the text when the flag was set and
 *   drew it (its drawInternal); 1.21.2's drawInternal takes an inverse-depth flag instead, which 1.21.2's own
 *   drawInBatch(String, ...) passes as true -- drawn the same way here after 1.21.1's shaping.
 * - drawInBatch(Component, ..., backgroundColor, light): 1.21.1's body, the component's visual order text drawn by
 *   drawInBatch(FormattedCharSequence, ...), which 1.21.2 keeps.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_327.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderFontMixin {

    @Shadow
    public abstract String method_1721(String text);

    @Shadow
    private int method_27529(String text, float x, float y, int color, boolean dropShadow, org.joml.Matrix4f matrix, net.minecraft.class_4597 buffers,
                             net.minecraft.class_327.class_6415 mode, int backgroundColor, int light, boolean inverseDepth) {
        throw new AssertionError();
    }

    public int method_27522(String text, float x, float y, int color, boolean dropShadow, org.joml.Matrix4f matrix, net.minecraft.class_4597 buffers,
                            net.minecraft.class_327.class_6415 mode, int backgroundColor, int light, boolean bidirectional) {
        if (bidirectional) {
            text = this.method_1721(text);
        }
        return this.method_27529(text, x, y, color, dropShadow, matrix, buffers, mode, backgroundColor, light, true);
    }

    public int method_30882(net.minecraft.class_2561 text, float x, float y, int color, boolean dropShadow, org.joml.Matrix4f matrix,
                            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int backgroundColor, int light) {
        return ((net.minecraft.class_327) (Object) this).method_22942(text.method_30937(), x, y, color, dropShadow, matrix, buffers, mode, backgroundColor, light);
    }
}
