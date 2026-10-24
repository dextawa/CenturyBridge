package top.dext.centurybridge.step.v1_21_4.compat.fapi.modelloading;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API 1.21.3's ModelResolver (gone in 1.21.4's model loading API): supplies the unbaked model for an id, asked before
 * the model file is loaded. A plugin context's resolveModel() event of these is asked again, first, where 1.21.4 hands each
 * model on load to the plugins (CbFapiModelLoading).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelResolver {
    /** resolveModel(context): the model for the context's id, or null */
    net.minecraft.class_1100 resolveModel(Context context);

    /** ModelResolver.Context: the id being resolved */
    interface Context {
        net.minecraft.class_2960 id();
    }
}
