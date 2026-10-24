package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex;

/**
 * 1.21.1 RenderLayerParent.getTextureLocation(entity), gone in 1.21.2 (see CbRenderTex). 1.21.1's vanilla implementations
 * were the renderers, whose class methods (CbRenderTexMixin and the generated mixins) win over this default; a 1.21.1
 * mod's implementation declares its own; anything else is a 1.21.2 class that never had the method. RenderLayerParent extends CbRenderTex.Legacy here, so a layer reaches
 * any parent's -- a renderer's, or a 1.21.1 mod's own implementation -- through it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3883.class)
public interface CbRenderTexParentMixin extends CbRenderTex.Legacy {

    /** 1.21.1 RenderLayerParent.getTextureLocation(T) */
    @Override
    default net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity) {
        // a renderer's own (CbRenderTexMixin's, or its put-back or overriding one) wins over this default; what reaches
        // here implements RenderLayerParent without being a renderer, and is not a 1.21.1 class (which implemented it)
        throw new AbstractMethodError(this.getClass().getName() + ".getTextureLocation(Entity): abstract in 1.21.1");
    }
}
