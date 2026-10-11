package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;

/**
 * Stand-in for fabric-api's removed BakedModelManagerHelper. Since fapi's
 * ExtraModelKey rework the manager is no longer queried by Identifier, so the
 * compat layer keeps its own Identifier -> key map: CbModelLoadingRegistry
 * files the key when the legacy provider registers the model, and the lookup
 * resolves through it here.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbBakedModelManagerHelper {

    static final Map<net.minecraft.class_2960, ExtraModelKey<net.minecraft.class_1087>> CB_KEYS =
        new ConcurrentHashMap<>();

    public static net.minecraft.class_1087 getModel(net.minecraft.class_1092 manager,
                                                    net.minecraft.class_2960 id) {
        ExtraModelKey<net.minecraft.class_1087> key = CB_KEYS.get(id);
        return key == null ? null : ((FabricBakedModelManager) manager).getModel(key);
    }

    private CbBakedModelManagerHelper() {
    }
}
