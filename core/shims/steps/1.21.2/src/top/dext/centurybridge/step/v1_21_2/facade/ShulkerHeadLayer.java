package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderLayers;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateGen;

/**
 * 1.21.1's ShulkerHeadLayer (net/minecraft/class_944, classRenames), gone in 1.21.2 (ShulkerRenderer draws the head with
 * the rest of the model): the shulker's head drawn again in the entity's colour. 1.21.1's constructor and body: the
 * head part of the parent ShulkerModel with the texture of the shulker's colour (ShulkerRenderer.getTextureLocation),
 * RenderType.entitySolid, and the entity's overlay. 1.21.2 calls the layer with the render state; the 1.21.1 render
 * runs on the entity the state was extracted from, with the arguments 1.21.1's LivingEntityRenderer computed
 * (CbRenderStateGen.st111, as for any 1.21.1 layer), so a subclass's override of either render is reached.
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class ShulkerHeadLayer extends net.minecraft.class_3887 implements CbRenderStateGen.St111 {

    public ShulkerHeadLayer(net.minecraft.class_3883 parent) {
        super(parent);
    }

    /** 1.21.1 render(poseStack, buffers, light, shulker, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch) */
    public void method_4115(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_1606 shulker,
                            float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        net.minecraft.class_2960 texture = net.minecraft.class_943.method_37105(shulker.method_7121());
        net.minecraft.class_4588 consumer = buffers.getBuffer(net.minecraft.class_1921.method_23572(texture));
        ((net.minecraft.class_602) this.method_17165()).field_3554.method_22698(poseStack, consumer, light,
            CbRenderLayers.method_23622(shulker, 0.0F));
    }

    /** 1.21.1 RenderLayer.render(.., entity, ..): the shulker's */
    @Override
    public void method_4199(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_1297 entity,
                            float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.method_4115(poseStack, buffers, light, (net.minecraft.class_1606) entity, limbSwing, limbSwingAmount, partialTick, ageInTicks,
            netHeadYaw, headPitch);
    }

    /** 1.21.2 RenderLayer.render(.., state, yRot, xRot): 1.21.1's on the entity of the state */
    @Override
    public void method_4199(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_10017 state,
                            float yRot, float xRot) {
        CbRenderStateGen.st111(this, poseStack, buffers, light, state, yRot, xRot);
    }
}
