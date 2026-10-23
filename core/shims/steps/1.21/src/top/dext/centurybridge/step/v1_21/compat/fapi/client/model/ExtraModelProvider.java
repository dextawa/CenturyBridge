package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API 1.20.6's ExtraModelProvider (fabric-models-v0, gone from 1.21's Fabric API): hands extra model ids to
 * load and bake. Renamed here (classRenames, s6fin-fapi.json); CbFapiModels adds each id with 1.21's
 * ModelLoadingPlugin.Context.addModels, as fabric-models-v0 itself did.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ExtraModelProvider {
    void provideExtraModels(net.minecraft.class_3300 manager, Consumer<net.minecraft.class_2960> out);
}
