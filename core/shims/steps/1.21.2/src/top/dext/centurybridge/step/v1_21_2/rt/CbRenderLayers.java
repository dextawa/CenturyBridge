package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * RenderLayer / LivingEntityRenderer statics in their 1.21.1 shapes (staticRedirects in s7render.json), which took the
 * entity where 1.21.2's take its render state.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLayers {
    private CbRenderLayers() {
    }

    /**
     * LivingEntityRenderer.getOverlayCoords(entity, whiteOverlayProgress): 1.21.1's body, as 1.21.2's state version
     * computes it from the state's hasRedOverlay (hurtTime > 0 || deathTime > 0)
     */
    public static int method_23622(net.minecraft.class_1309 entity, float whiteOverlayProgress) {
        return net.minecraft.class_4608.method_23625(net.minecraft.class_4608.method_23210(whiteOverlayProgress),
            net.minecraft.class_4608.method_23212(entity.field_6235 > 0 || entity.field_6213 > 0));
    }

    /**
     * RenderLayer.renderColoredCutoutModel(model, texture, poseStack, buffers, light, entity, color): 1.21.1's body --
     * the model drawn cutout with the entity's overlay
     */
    public static void method_23199(net.minecraft.class_583<?> model, net.minecraft.class_2960 texture, net.minecraft.class_4587 poseStack,
                                    net.minecraft.class_4597 buffers, int light, net.minecraft.class_1309 entity, int color) {
        net.minecraft.class_4588 consumer = buffers.getBuffer(net.minecraft.class_1921.method_23578(texture));
        model.method_62100(poseStack, consumer, light, method_23622(entity, 0.0F), color);
    }

    /**
     * RenderLayer.coloredCutoutModelCopyLayerRender(parent, model, texture, poseStack, buffers, light, entity, limbSwing,
     * limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, color): 1.21.1's body -- unless the entity is
     * invisible, the parent's attackTime / riding / young copied onto the model (copyPropertiesTo), the model prepared
     * and posed from the entity with the numbers passed (prepareMobModel / setupAnim, run as 1.21.2 poses a model from
     * the entity's state: CbRenderModelCalls), then drawn
     */
    public static void method_23196(net.minecraft.class_583<?> parent, net.minecraft.class_583<?> model, net.minecraft.class_2960 texture,
                                    net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                    net.minecraft.class_1309 entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                    float netHeadYaw, float headPitch, float partialTick, int color) {
        if (entity.method_5767()) {
            return;
        }
        CbRenderModelFields.Pose from = (CbRenderModelFields.Pose) parent;
        ((CbRenderModelFields.Pose) model).cb$pose(from.cb$attackTime(), from.cb$riding(), from.cb$young());
        CbRenderModelCalls.prepare(model, entity, partialTick);
        CbRenderModelCalls.setupAnim(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
            net.minecraft.class_10042.class);
        method_23199(model, texture, poseStack, buffers, light, entity, color);
    }

    /**
     * CustomHeadLayer.translateToHead(poseStack, villager): 1.21.1's body (1.21.2's takes the renderer's head
     * Transforms instead of the villager flag)
     */
    public static void method_32798(net.minecraft.class_4587 poseStack, boolean villager) {
        poseStack.method_46416(0.0F, -0.25F, 0.0F);
        poseStack.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(180.0F));
        poseStack.method_22905(0.625F, -0.625F, -0.625F);
        if (villager) {
            poseStack.method_46416(0.0F, 0.1875F, 0.0F);
        }
    }
}
