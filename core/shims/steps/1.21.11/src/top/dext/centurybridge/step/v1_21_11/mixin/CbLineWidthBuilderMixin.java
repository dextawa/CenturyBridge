package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.class_287;
import net.minecraft.class_4588;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbLineWidthTarget;

/**
 * BufferBuilder: a vertex left without its line width when the next one starts (or the mesh is built) gets the
 * width 1.21.10 drew the render type's lines at (rt/CbLineWidth), where 1.21.11 would refuse the vertex ("Missing
 * elements in vertex: LineWidth"). 1.21.11's own callers always write the width: for them nothing changes.
 */
@Mixin(class_287.class)
public abstract class CbLineWidthBuilderMixin implements CbLineWidthTarget {
    @Shadow
    private int field_52077;

    @Unique
    private float cb$lineWidth = Float.NaN;

    @Shadow
    public abstract class_4588 method_75298(float width);

    @Override
    public void cb$lineWidth(float width) {
        this.cb$lineWidth = width;
    }

    @Inject(method = "method_60806", at = @At("HEAD"))
    private void cb$fillLineWidth(CallbackInfo ci) {
        if (!Float.isNaN(this.cb$lineWidth) && (this.field_52077 & VertexFormatElement.LINE_WIDTH.mask()) != 0) {
            this.method_75298(this.cb$lineWidth);
        }
    }
}
