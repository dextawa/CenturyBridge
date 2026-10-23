package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex;

/**
 * 1.21.1 EntityRenderer.getTextureLocation(entity), abstract there and gone in 1.21.2 (see CbRenderTex). Every 1.21.1
 * renderer answered it: the generated CbRenderTex<n>Mixin put back each 1.21.1 renderer's own answer; here, the one
 * of a living renderer 1.21.1 did not have (its 1.21.2 texture of the entity's state). A renderer that is neither had
 * no 1.21.1 implementation to give back: the call is the AbstractMethodError it would have been.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_897.class)
public abstract class CbRenderTexMixin implements CbRenderTex.Renderer, CbRenderTex.Legacy {

    @Shadow
    @Final
    private net.minecraft.class_10017 field_53189;

    @Override
    public net.minecraft.class_10017 cb$reusedState() {
        return this.field_53189;
    }

    /** 1.21.1 EntityRenderer.getTextureLocation(T) */
    @Override
    public net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity) {
        if ((Object) this instanceof net.minecraft.class_922) {
            return CbRenderTex.living(this, entity);
        }
        throw new AbstractMethodError(this.getClass().getName() + ".getTextureLocation(Entity): abstract in 1.21.1, "
            + "and this renderer did not exist there");
    }
}
