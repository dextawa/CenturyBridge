package top.dext.centurybridge.step.v1_21_4.compat.fapi.v0;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.CbItemColorProviderRegistry;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColor;

/**
 * Fabric API's fabric-rendering-v0 ColorProviderRegistry (the module is gone in 1.21.4's Fabric API). v0 forwarded
 * both registries to rendering-v1's: ITEM to the item colour providers (kept by this layer, rendering-v1's ITEM being
 * gone too), BLOCK to rendering-v1's BLOCK, which 1.21.4 still has.
 */
@Environment(EnvType.CLIENT)
public interface ColorProviderRegistry<T, Provider> {

    ColorProviderRegistry<net.minecraft.class_1935, ItemColor> ITEM = new ColorProviderRegistry<>() {
        @Override
        public void register(ItemColor provider, net.minecraft.class_1935... items) {
            CbItemColorProviderRegistry.ITEM.register(provider, items);
        }

        @Override
        public ItemColor get(net.minecraft.class_1935 item) {
            return CbItemColorProviderRegistry.ITEM.get(item);
        }
    };

    ColorProviderRegistry<net.minecraft.class_2248, net.minecraft.class_322> BLOCK = new ColorProviderRegistry<>() {
        @Override
        public void register(net.minecraft.class_322 provider, net.minecraft.class_2248... blocks) {
            net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(provider, blocks);
        }

        @Override
        public net.minecraft.class_322 get(net.minecraft.class_2248 block) {
            return net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.get(block);
        }
    };

    @SuppressWarnings("unchecked")
    void register(Provider provider, T... objects);

    Provider get(T object);
}
