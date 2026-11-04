package top.dext.centurybridge.step.v1_21.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_296;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbRenderFormats;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/** VertexFormatElement.clearBufferState(int), restored for 1.20.6 subclasses of the native record. */
@Environment(EnvType.CLIENT)
@Mixin(value = class_296.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbVertexFormatElementMixin implements CbRenderFormats.LegacyClearState {
    @Override
    public void method_22653(int elementIndex) {
        CbRenderFormats.cbClearBufferState((class_296) (Object) this, elementIndex);
    }
}
