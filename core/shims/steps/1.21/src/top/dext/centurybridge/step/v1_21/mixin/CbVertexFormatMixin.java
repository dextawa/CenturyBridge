package top.dext.centurybridge.step.v1_21.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_293;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.dext.centurybridge.step.v1_21.rt.CbRenderFormats;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/** Native format clearing dispatches through each element's restored virtual clearBufferState. */
@Environment(EnvType.CLIENT)
@Mixin(value = class_293.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbVertexFormatMixin {
    @Overwrite
    public void method_34450() {
        CbRenderFormats.cbClearFormat((class_293) (Object) this);
    }
}
