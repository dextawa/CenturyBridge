package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.rt.CbFapiModels;

/**
 * Fabric API 1.20.6's ModelLoadingRegistry (fabric-models-v0, gone from 1.21's Fabric API): the registration point
 * of the four legacy model hooks. Renamed here (classRenames, s6fin-fapi.json); its INSTANCE is CbFapiModels, which
 * runs them on 1.21's ModelLoadingPlugin as fabric-models-v0 ran them on 1.20.6's.
 */
@Environment(EnvType.CLIENT)
public interface ModelLoadingRegistry {
    ModelLoadingRegistry INSTANCE = new CbFapiModels();

    void registerModelProvider(ExtraModelProvider provider);

    void registerAppender(ModelAppender appender);

    void registerResourceProvider(Function<net.minecraft.class_3300, ModelResourceProvider> providerSupplier);

    void registerVariantProvider(Function<net.minecraft.class_3300, ModelVariantProvider> providerSupplier);
}
