package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Fabric API 1.20.6's ModelResourceProvider (fabric-models-v0): supplies the unbaked model of a model id, or null. Renamed here. */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelResourceProvider {
    net.minecraft.class_1100 loadModelResource(net.minecraft.class_2960 id, ModelProviderContext context) throws ModelProviderException;
}
