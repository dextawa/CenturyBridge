package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ChestedHorseModel (net/minecraft/class_559, classRenames): the equine model with the two chests, shown when
 * the animal has a chest. 1.21.2's DonkeyModel is that model (the equine mesh with the chests, shown from
 * DonkeyRenderState.hasChest), so this is it: createBodyLayer is DonkeyModel's adult layer, and 1.21.1's setupAnim(entity,
 * ...) poses it from a state of the entity (CbRenderModelCalls, the mechanism of every 1.21.1 setupAnim the layer gives
 * back), which carries the chest flag 1.21.1 read off the entity.
 */
@Environment(EnvType.CLIENT)
public class ChestedHorseModel extends net.minecraft.class_9945 {

    public ChestedHorseModel(net.minecraft.class_630 root) {
        super(root);
    }

    /** createBodyLayer() */
    public static net.minecraft.class_5607 method_31987() {
        return net.minecraft.class_9945.method_62079();
    }

    /** setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch) */
    public void method_17076(net.minecraft.class_1492 entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls.setupAnim(this, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
            net.minecraft.class_10013.class);
    }
}
