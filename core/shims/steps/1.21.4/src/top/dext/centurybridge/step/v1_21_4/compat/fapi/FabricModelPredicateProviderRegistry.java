package top.dext.centurybridge.step.v1_21_4.compat.fapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.facade.ClampedItemPropertyFunction;
import top.dext.centurybridge.step.v1_21_4.facade.ItemProperties;

/**
 * Fabric API's FabricModelPredicateProviderRegistry (object-builder-api-v1, removed with 1.21.4's item model rework):
 * registers model predicates into ItemProperties, as it did on 1.21.3 (Fabric API invoked ItemProperties' private
 * registerGeneric / register). The converted item model definitions' legacy_overrides read them there.
 */
@Environment(EnvType.CLIENT)
public class FabricModelPredicateProviderRegistry {

    public FabricModelPredicateProviderRegistry() {
    }

    /** a predicate every item answers */
    public static void register(net.minecraft.class_2960 id, ClampedItemPropertyFunction provider) {
        ItemProperties.method_27881(id, provider);
    }

    /** a predicate for one item */
    public static void register(net.minecraft.class_1792 item, net.minecraft.class_2960 id, ClampedItemPropertyFunction provider) {
        ItemProperties.method_27879(item, id, provider);
    }
}
