package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Fabric API 1.20.6's ModelVariantProvider (fabric-models-v0): supplies the unbaked model of a model location, or null. Renamed here. */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelVariantProvider {
    net.minecraft.class_1100 loadModelVariant(net.minecraft.class_1091 location, ModelProviderContext context) throws ModelProviderException;
}
