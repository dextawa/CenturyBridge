package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Fabric API 1.20.6's ModelProviderContext (fabric-models-v0): loads a model by id for a provider. Renamed here. */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelProviderContext {
    net.minecraft.class_1100 loadModel(net.minecraft.class_2960 id);
}
