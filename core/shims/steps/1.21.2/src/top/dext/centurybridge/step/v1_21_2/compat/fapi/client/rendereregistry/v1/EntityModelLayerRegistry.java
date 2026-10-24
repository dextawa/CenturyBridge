package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendereregistry.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;

/**
 * 1.21.1 Fabric API's fabric-renderer-registries-v1 (deprecated, dropped by 1.21.2's Fabric API; classRenames +
 * depRenames) EntityModelLayerRegistry: the module's own body, which delegated to fabric-rendering-v1's same-named API, kept by 1.21.2's.
 */
@Environment(EnvType.CLIENT)
public final class EntityModelLayerRegistry {

    private EntityModelLayerRegistry() {
    }

    public static void registerModelLayer(net.minecraft.class_5601 layer, TexturedModelDataProvider provider) {
        net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.registerModelLayer(layer, Objects.requireNonNull(provider)::createModelData);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface TexturedModelDataProvider {
        net.minecraft.class_5607 createModelData();
    }
}
