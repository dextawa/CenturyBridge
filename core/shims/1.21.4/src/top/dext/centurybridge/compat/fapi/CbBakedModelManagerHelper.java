package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;

/**
 * Stand-in for fabric-api's removed BakedModelManagerHelper: the lookup moved
 * to an interface fabric injects into BakedModelManager itself.
 */
public final class CbBakedModelManagerHelper {

    public static net.minecraft.class_1087 getModel(net.minecraft.class_1092 manager,
                                                    net.minecraft.class_2960 id) {
        return ((FabricBakedModelManager) manager).getModel(id);
    }

    private CbBakedModelManagerHelper() {
    }
}
