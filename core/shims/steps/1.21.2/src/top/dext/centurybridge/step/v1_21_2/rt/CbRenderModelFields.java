package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The public pose fields 1.21.1's models carried and 1.21.2 moved into the render state.
 *
 * 1.21.1 EntityModel: attackTime (field_3447), riding (field_3449), young (field_3448), set by LivingEntityRenderer.render
 * before setupAnim (class_922.method_4054 0..37: this.getAttackAnim(entity, partialTick) -- the renderer's, see
 * CbRenderLiving.attackAnim --, entity.isPassenger(), entity.isBaby())
 * and read by the model's setupAnim. 1.21.2 keeps none of them (the state has attackTime as HumanoidRenderState.attackTime,
 * isPassenger, isBaby). The mixins put the fields back (CbRenderModelFieldsMixin); a 1.21.1 model reads them in its own
 * setupAnim, so they are set, as 1.21.1 set them, right before it runs ({@link #legacy}).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModelFields {

    /** EntityModel's attackTime / riding / young, put back on the model */
    public interface Pose {
        void cb$pose(float attackTime, boolean riding, boolean young);
    }

    private CbRenderModelFields() {
    }

    /** what 1.21.1's LivingEntityRenderer.render set on its model before posing it */
    public static void legacy(net.minecraft.class_583<?> model, net.minecraft.class_1309 entity, float partialTick) {
        ((Pose) model).cb$pose(CbRenderLiving.attackAnim(model, entity, partialTick), entity.method_5765(), entity.method_6109());
    }
}
