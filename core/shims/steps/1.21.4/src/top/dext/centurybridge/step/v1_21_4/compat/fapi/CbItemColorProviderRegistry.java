package top.dext.centurybridge.step.v1_21_4.compat.fapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColor;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemColors;

/**
 * Fabric API rendering-v1 ColorProviderRegistry.ITEM, removed in 1.21.4 (the interface and its BLOCK remain): a
 * 1.21.3 mod's read of the field gets this registry (fieldGetters), which keeps the providers in CbItemColors.
 */
@Environment(EnvType.CLIENT)
public final class CbItemColorProviderRegistry implements ColorProviderRegistry<net.minecraft.class_1935, ItemColor> {

    public static final CbItemColorProviderRegistry ITEM = new CbItemColorProviderRegistry();

    private CbItemColorProviderRegistry() {
    }

    /** the field read ColorProviderRegistry.ITEM becomes */
    public static ColorProviderRegistry<net.minecraft.class_1935, ItemColor> item() {
        return ITEM;
    }

    @Override
    public void register(ItemColor provider, net.minecraft.class_1935... items) {
        CbItemColors.register(provider, items);
    }

    @Override
    public ItemColor get(net.minecraft.class_1935 item) {
        return CbItemColors.get(item);
    }
}
