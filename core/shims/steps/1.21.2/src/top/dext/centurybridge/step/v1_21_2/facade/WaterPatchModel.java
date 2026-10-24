package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's WaterPatchModel (net/minecraft/class_7755, classRenames), gone in 1.21.2 (the water patch is a model of its
 * own there, ModelLayers.BOAT_WATER_PATCH): a boat model's part masking the water inside it. 1.21.2's BoatModel
 * implements it again with 1.21.1's waterPatch() (CbRenderBoatModelMixin), as 1.21.1's BoatModel and ChestBoatModel did.
 */
@Environment(EnvType.CLIENT)
public interface WaterPatchModel {

    /** waterPatch() */
    net.minecraft.class_630 method_22954();
}
