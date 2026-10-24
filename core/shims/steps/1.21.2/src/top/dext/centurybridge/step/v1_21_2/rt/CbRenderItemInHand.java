package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ItemInHandLayer / PlayerItemInHandLayer.renderArmWithItem(entity, stack, displayContext, arm, poseStack,
 * buffers, light) over 1.21.2's renderArmWithItem(state, bakedModel, stack, displayContext, arm, ...).
 *
 * <p>1.21.1 drew the held item through the ItemInHandRenderer with the entity (ItemInHandRenderer.renderItem(entity,
 * stack, context, leftHand, ...)); 1.21.2 extracts the item's baked model into the render state and draws it with the
 * ItemRenderer. The 1.21.1 methods are put back on the vanilla layers with 1.21.1's bodies (CbRenderItemInHandMixin,
 * CbRenderPlayerItemInHandMixin; compared in both jars: the same hand transform, rotations and offset), so a mod's call
 * or super call draws as 1.21.1 did. A mod layer's 1.21.1 override answers 1.21.2's calls (overrideHooks) with the entity
 * the state was extracted from and the stack 1.21.2 passes.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderItemInHand {

    /** ItemInHandLayer's 1.21.1 renderArmWithItem (CbRenderItemInHandMixin; a mod layer's override) */
    public interface Legacy {
        void method_4192(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack, net.minecraft.class_811 displayContext,
                         net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light);
    }

    private CbRenderItemInHand() {
    }

    private static net.minecraft.class_759 itemInHandRenderer() {
        return net.minecraft.class_310.method_1551().method_1561().method_43336();
    }

    /** 1.21.1 ItemInHandLayer.renderArmWithItem's body */
    @SuppressWarnings("rawtypes")
    public static void render(net.minecraft.class_989 layer, net.minecraft.class_1309 entity, net.minecraft.class_1799 stack,
                              net.minecraft.class_811 displayContext, net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack,
                              net.minecraft.class_4597 buffers, int light) {
        if (stack.method_7960()) {
            return;
        }
        poseStack.method_22903();
        ((net.minecraft.class_3881) layer.method_17165()).method_2803(arm, poseStack);
        poseStack.method_22907(net.minecraft.class_7833.field_40714.rotationDegrees(-90.0F));
        poseStack.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(180.0F));
        boolean left = arm == net.minecraft.class_1306.field_6182;
        poseStack.method_46416((float) (left ? -1 : 1) / 16.0F, 0.125F, -0.625F);
        itemInHandRenderer().method_3233(entity, stack, displayContext, left, poseStack, buffers, light);
        poseStack.method_22909();
    }

    /**
     * 1.21.1 PlayerItemInHandLayer.renderArmWithItem's body: a spyglass the entity is using (and not swinging) drawn at
     * the head (renderArmWithSpyglass), anything else as ItemInHandLayer draws it
     */
    @SuppressWarnings("rawtypes")
    public static void renderPlayer(net.minecraft.class_5697 layer, net.minecraft.class_1309 entity, net.minecraft.class_1799 stack,
                                    net.minecraft.class_811 displayContext, net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack,
                                    net.minecraft.class_4597 buffers, int light) {
        if (stack.method_31574(net.minecraft.class_1802.field_27070) && entity.method_6030() == stack && entity.field_6279 == 0) {
            renderSpyglass(layer, entity, stack, arm, poseStack, buffers, light);
        } else {
            render(layer, entity, stack, displayContext, arm, poseStack, buffers, light);
        }
    }

    /** 1.21.1 PlayerItemInHandLayer.renderArmWithSpyglass's body */
    @SuppressWarnings("rawtypes")
    public static void renderSpyglass(net.minecraft.class_5697 layer, net.minecraft.class_1309 entity, net.minecraft.class_1799 stack,
                                      net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light) {
        poseStack.method_22903();
        net.minecraft.class_630 head = ((net.minecraft.class_3882) layer.method_17165()).method_2838();
        float xRot = head.field_3654;
        head.field_3654 = net.minecraft.class_3532.method_15363(head.field_3654, -0.5235988F, 1.5707964F);
        head.method_22703(poseStack);
        head.field_3654 = xRot;
        CbRenderLayers.method_32798(poseStack, false);
        boolean left = arm == net.minecraft.class_1306.field_6182;
        poseStack.method_46416((left ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
        itemInHandRenderer().method_3233(entity, stack, net.minecraft.class_811.field_4316, false, poseStack, buffers, light);
        poseStack.method_22909();
    }

    /** 1.21.2's renderArmWithItem on a mod layer: its 1.21.1 override, with the entity of the state */
    public static void renderArmWithItem(Legacy self, net.minecraft.class_10042 state, net.minecraft.class_1087 model, net.minecraft.class_1799 stack,
                                         net.minecraft.class_811 displayContext, net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack,
                                         net.minecraft.class_4597 buffers, int light) {
        if (!(CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1309 entity)) {
            throw new IllegalStateException("1.21.1 ItemInHandLayer.renderArmWithItem: a render state no living entity was extracted into");
        }
        self.method_4192(entity, stack, displayContext, arm, poseStack, buffers, light);
    }
}
