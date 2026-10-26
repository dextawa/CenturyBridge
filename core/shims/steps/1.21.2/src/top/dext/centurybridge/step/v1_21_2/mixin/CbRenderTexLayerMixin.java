package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderLiving;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 RenderLayer.getTextureLocation(entity), gone in 1.21.2: it asked the layer's parent, as 1.21.1 did
 * (class_3887.method_23194: renderer.getTextureLocation(entity); the parent's answer is CbRenderTexParentMixin's).
 * Also hands the rt code the layer's parent (CbRenderLiving.Layer).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_3887.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderTexLayerMixin implements CbRenderLiving.Layer {

    @Shadow
    @Final
    private net.minecraft.class_3883<?, ?> field_17155;

    @Override
    public net.minecraft.class_3883<?, ?> cb$parent() {
        return this.field_17155;
    }

    /** 1.21.1 RenderLayer.getTextureLocation(T) */
    protected net.minecraft.class_2960 method_23194(net.minecraft.class_1297 entity) {
        return ((CbRenderTex.Legacy) this.field_17155).method_3931(entity);
    }
}
