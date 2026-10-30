package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1092;
import net.minecraft.class_5617;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's EntityRendererProvider.Context.getModelManager(), which 1.21.9 dropped. 1.21.8's body (javap -c):
 * blockRenderDispatcher.getBlockModelShaper().getModelManager(), all of which 1.21.9 keeps. Read through the context's
 * public getBlockRenderDispatcher(), not a shadow of the field, so the later steps' wiring carries it on as the layer
 * climbs (26.1 drops the field). 1.21.9 never calls it.
 */
@Mixin(value = class_5617.class_5618.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRendererContextRestoredMixin {

    /** EntityRendererProvider.Context.getModelManager() */
    public class_1092 method_48481() {
        return ((class_5617.class_5618) (Object) this).method_43337().method_3351().method_3333();
    }
}
