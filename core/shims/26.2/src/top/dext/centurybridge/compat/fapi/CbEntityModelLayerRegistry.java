package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

/**
 * fabric-rendering-v1's EntityModelLayerRegistry as 1.20.x mods call it (27
 * corpus mods). 26.x renamed it to ModelLayerRegistry and its provider's SAM
 * from createModelData() to createLayerDefinition(): a pure class rename would
 * leave every 1.20.1 method reference an AbstractMethodError, so the old SAM
 * ({@link CbTexturedModelDataProvider}) is adapted here.
 */
public final class CbEntityModelLayerRegistry {

    public static void registerModelLayer(ModelLayerLocation layer, CbTexturedModelDataProvider provider) {
        ModelLayerRegistry.registerModelLayer(layer, provider::createModelData);
    }

    private CbEntityModelLayerRegistry() {
    }
}
