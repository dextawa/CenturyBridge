package top.dext.centurybridge.compat.fapi;

import net.minecraft.client.model.geom.builders.LayerDefinition;

/** was EntityModelLayerRegistry.TexturedModelDataProvider (SAM createModelData) */
@FunctionalInterface
public interface CbTexturedModelDataProvider {
    LayerDefinition createModelData();
}
