package top.dext.centurybridge.compat.fapi;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import top.dext.centurybridge.facade.v26_2.class_5272;
import top.dext.centurybridge.facade.v26_2.class_6395;

/**
 * fabric-object-builder-api-v1's FabricModelPredicateProviderRegistry as 1.20.x mods call it:
 * the public face of vanilla's private ModelPredicateProviderRegistry.register. Fabric API
 * dropped it with the registry in 1.21.4; both overloads land in the old registry CenturyBridge
 * keeps (class_5272), which the converted legacy_overrides item models read.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbFabricModelPredicateProviderRegistry {

    /** register(id, provider): a provider for every item */
    public static void register(Identifier id, class_6395 provider) {
        class_5272.method_27881(id, provider);
    }

    /** register(item, id, provider) */
    public static void register(Item item, Identifier id, class_6395 provider) {
        class_5272.method_27879(item, id, provider);
    }

    private CbFabricModelPredicateProviderRegistry() {
    }
}
