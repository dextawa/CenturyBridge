package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

/**
 * 1.21.11 ChestRenderer.singleModel / doubleLeftModel / doubleRightModel (class_826.field_53165..53167, opened by
 * access wideners): 1.21.11's constructor baked ChestModel from ModelLayers.CHEST / DOUBLE_CHEST_LEFT /
 * DOUBLE_CHEST_RIGHT. 26.1 (javap) bakes the same three layers into its models field
 * (MultiblockChestResources(single, left, right) = LAYERS.map(bake)); each read answers that component.
 */
@Environment(EnvType.CLIENT)
public final class CbWallChestModels {
    private CbWallChestModels() {
    }

    public static ChestModel singleModel(ChestRenderer<?> r) {
        return r.models.single();
    }

    public static ChestModel doubleLeftModel(ChestRenderer<?> r) {
        return r.models.left();
    }

    public static ChestModel doubleRightModel(ChestRenderer<?> r) {
        return r.models.right();
    }
}
