package top.dext.centurybridge.rt.v1_21_4;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.class_1935;
import top.dext.centurybridge.facade.v1_21_4.class_326;

/**
 * Fabric API's ColorProviderRegistry.ITEM, which 1.21.4's Fabric API dropped along with
 * vanilla's ItemColors: a mod's colour providers are kept per item and answered by the
 * legacy_color tint source the converted item model definitions carry.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbItemColorRegistry implements ColorProviderRegistry<class_1935, class_326> {

    public static final CbItemColorRegistry INSTANCE = new CbItemColorRegistry();

    @Override
    public void register(class_326 provider, class_1935... items) {
        for (class_1935 item : items) {
            CbLegacyItemModels.ITEM_COLORS.put(item.method_8389(), provider);
        }
    }

    @Override
    public class_326 get(class_1935 item) {
        return CbLegacyItemModels.ITEM_COLORS.get(item.method_8389());
    }

    private CbItemColorRegistry() {
    }
}
