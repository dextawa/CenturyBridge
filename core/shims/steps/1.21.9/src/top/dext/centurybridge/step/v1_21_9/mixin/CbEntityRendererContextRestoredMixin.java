package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1092;
import net.minecraft.class_5617;
import net.minecraft.class_776;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's EntityRendererProvider.Context.getModelManager(), which 1.21.9 dropped. 1.21.8's body (javap -c):
 * blockRenderDispatcher.getBlockModelShaper().getModelManager(), all of which 1.21.9 keeps. 1.21.9 never calls it.
 */
@Mixin(value = class_5617.class_5618.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRendererContextRestoredMixin {

    @Shadow
    @Final
    private class_776 field_38888;

    /** EntityRendererProvider.Context.getModelManager() */
    public class_1092 method_48481() {
        return this.field_38888.method_3351().method_3333();
    }
}
