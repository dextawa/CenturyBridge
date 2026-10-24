package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 CustomHeadLayer's scale (scaleX, scaleY, scaleZ): the head item was drawn after poseStack.scale(scaleX,
 * scaleY, scaleZ) -- 1.21.1's HumanoidMobRenderer(context, model, shadow, scaleX, scaleY, scaleZ) handed them to its head
 * layer. 1.21.2's head layer takes Transforms (skull offsets and one horizontal scale, drawn as scale(h, 1, h)); a layer
 * built from 1.21.1's three scales keeps them (CbRenderHeadLayerMixin) and draws with 1.21.1's scale, the transforms
 * left at 1.21.2's default (no skull offset, scale 1: what 1.21.1's layer drew with besides its scale).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderHeads {

    /** CustomHeadLayer's 1.21.1 scale (CbRenderHeadLayerMixin) */
    public interface Scaled {
        void cb$setScale(float x, float y, float z);
    }

    private CbRenderHeads() {
    }

    public static net.minecraft.class_976.class_9994 defaultTransforms() {
        return net.minecraft.class_976.class_9994.field_53212;
    }

    /** after new CustomHeadLayer(parent, modelSet, scaleX, scaleY, scaleZ, itemInHandRenderer) */
    public static void headScale(net.minecraft.class_976<?, ?> layer, float x, float y, float z) {
        ((Scaled) layer).cb$setScale(x, y, z);
    }

    /** after new HumanoidMobRenderer(context, model, shadow, scaleX, scaleY, scaleZ): the head layer it added */
    public static void humanoidHeadScale(net.minecraft.class_909<?, ?, ?> renderer, float x, float y, float z) {
        for (Object layer : renderer.field_4738) {
            if (layer instanceof net.minecraft.class_976<?, ?> head) {
                headScale(head, x, y, z);
            }
        }
    }
}
