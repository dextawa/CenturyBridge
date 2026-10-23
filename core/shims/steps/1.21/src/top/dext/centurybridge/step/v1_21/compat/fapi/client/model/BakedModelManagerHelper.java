package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API 1.20.6's BakedModelManagerHelper (fabric-models-v0): the baked model of a model id added through the
 * model loading hooks. Renamed here; 1.21's Fabric API answers it on the model manager itself
 * (FabricBakedModelManager.getModel, which the helper called).
 */
@Environment(EnvType.CLIENT)
public final class BakedModelManagerHelper {
    private BakedModelManagerHelper() {
    }

    public static net.minecraft.class_1087 getModel(net.minecraft.class_1092 manager, net.minecraft.class_2960 id) {
        return ((net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager) manager).getModel(id);
    }
}
