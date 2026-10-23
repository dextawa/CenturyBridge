package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10186;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_8053;
import net.minecraft.class_8054;
import net.minecraft.class_8056;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestEquipmentAssetsInvoker;

/**
 * The client-typed half of 1.21.3's equipment model API (CbRestEquipment): members whose shape names
 * EquipmentModel / EquipmentClientInfo.LayerType, a client class in 1.21.4.
 */
@Environment(EnvType.CLIENT)
public final class CbRestEquipmentClient {

    private CbRestEquipmentClient() {
    }

    /**
     * 1.21.3 EquipmentModels.bootstrap(output): vanilla's equipment models by model id. 1.21.4 keeps the same
     * models in EquipmentAssetProvider's bootstrap, by asset key (whose location is the 1.21.3 id).
     */
    public static void method_64013(BiConsumer<class_2960, class_10186> output) {
        CbRestEquipmentAssetsInvoker.cb$bootstrap((key, info) -> output.accept(key.method_29177(), info));
    }

    /**
     * 1.21.3 ArmorTrim.getTexture(layerType, modelId): the pattern's asset id with the path
     * trims/entity/(layer)/(path)_(material suffix); the suffix is the material's override for that
     * equipment model (1.21.4 keys the overrides by asset key) or its asset name. 1.21.3's body.
     */
    public static class_2960 method_64019(class_8053 trim, class_10186.class_10190 layerType, class_2960 modelId) {
        class_2960 patternAsset = ((class_8056) trim.comp_3180().comp_349()).comp_1213();
        class_8054 material = (class_8054) trim.comp_3179().comp_349();
        class_5321<?> key = CbRestEquipment.asset(modelId);
        String suffix = material.comp_1237().get(key);
        String s = suffix != null ? suffix : material.comp_1208();
        return patternAsset.method_45134(path -> "trims/entity/" + layerType.method_15434() + "/" + path + "_" + s);
    }
}
