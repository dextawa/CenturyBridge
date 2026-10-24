package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's EnderDragonRenderer.DragonModel (net/minecraft/class_895$class_625, classRenames): the dragon model posed
 * from the dragon entity (prepareMobModel kept the dragon and the partial tick, setupAnim / renderToBuffer posed the
 * neck, tail, wings and jaw from its flap time and flight history). 1.21.2's EnderDragonModel poses the same parts from
 * an EnderDragonRenderState, which EnderDragonRenderer.extractRenderState fills from the dragon (flap time, death
 * time, the flight history at that partial tick) -- 1.21.1's inputs. So this model is 1.21.2's, baked from 1.21.2's
 * ender dragon layer (EnderDragonRenderer.createBodyLayer answers it, rt/CbRenderDragons), and its 1.21.1 setupAnim
 * poses it from a state of the dragon at the partial tick prepareMobModel kept (CbRenderModelCalls, the mechanism of
 * every 1.21.1 setupAnim the layer gives back); renderToBuffer draws the posed root.
 */
@Environment(EnvType.CLIENT)
public class DragonModel extends net.minecraft.class_625 {

    public DragonModel(net.minecraft.class_630 root) {
        super(root);
    }

    /** prepareMobModel(dragon, limbSwing, limbSwingAmount, partialTick): the dragon and partial tick kept for setupAnim */
    public void method_23620(net.minecraft.class_1510 dragon, float limbSwing, float limbSwingAmount, float partialTick) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls.prepare(this, dragon, partialTick);
    }

    /** setupAnim(dragon, ...): 1.21.2's setupAnim on the state of the dragon (CbRenderModelCalls) */
    public void method_23621(net.minecraft.class_1510 dragon, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls.setupAnim(this, dragon, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
            net.minecraft.class_10015.class);
    }

    /** renderToBuffer(poseStack, buffer, light, overlay, color): the posed model */
    public void method_2828(net.minecraft.class_4587 poseStack, net.minecraft.class_4588 buffer, int light, int overlay, int color) {
        this.method_63512().method_22699(poseStack, buffer, light, overlay, color);
    }
}
