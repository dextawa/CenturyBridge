package top.dext.centurybridge.compat.fapi;

import net.minecraft.client.model.geom.builders.LayerDefinition;

/** was EntityModelLayerRegistry.TexturedModelDataProvider (SAM createModelData) */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbTexturedModelDataProvider {
    LayerDefinition createModelData();
}
