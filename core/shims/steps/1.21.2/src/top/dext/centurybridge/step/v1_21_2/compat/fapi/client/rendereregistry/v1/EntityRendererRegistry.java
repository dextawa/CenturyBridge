package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendereregistry.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 Fabric API's fabric-renderer-registries-v1 (deprecated, dropped by 1.21.2's Fabric API; classRenames +
 * depRenames) EntityRendererRegistry: the module's own body, which delegated to fabric-rendering-v1's same-named API, kept by 1.21.2's.
 */
@Environment(EnvType.CLIENT)
public interface EntityRendererRegistry {

    EntityRendererRegistry INSTANCE = new EntityRendererRegistry() {
        @Override
        public <T extends net.minecraft.class_1297> void register(net.minecraft.class_1299<? extends T> type, net.minecraft.class_5617<T> provider) {
            net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(type, provider);
        }
    };

    <E extends net.minecraft.class_1297> void register(net.minecraft.class_1299<? extends E> type, net.minecraft.class_5617<E> provider);
}
