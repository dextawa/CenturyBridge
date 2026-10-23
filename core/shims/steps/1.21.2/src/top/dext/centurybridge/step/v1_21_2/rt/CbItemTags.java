package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3497;
import net.minecraft.class_3503;

/**
 * 1.21.1's TagLoader / TagEntry.Lookup shapes on 1.21.2. 1.21.1's loader looked elements up by id alone (a
 * function); 1.21.2's lookup is also told whether the entry is required, which only its pending-tag lookups act on:
 * a 1.21.1 function answers the same either way. loadAndBuild(resources) is 1.21.2's build(load(resources)), what
 * 1.21.1's body was. A 1.21.1 call of Lookup.element(id) asks with "not required": 1.21.1's lookups only looked.
 */
public final class CbItemTags {

    /** TagLoader(Function idToValue, directory): the function as 1.21.2's element lookup */
    public static <T> class_3503.class_10272<T> lookup(Function<class_2960, Optional<? extends T>> idToValue) {
        return (id, required) -> idToValue.apply(id);
    }

    /** a 1.21.1 TagLoader subclass overriding loadAndBuild(resources) (adapterHooks) */
    public interface LegacyLoadAndBuild {
        Map<class_2960, Collection<?>> method_33176(class_3300 resources);
    }

    /** loadAndBuild(resources): the class's override, else 1.21.1's body */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map method_33176(class_3503 loader, class_3300 resources) {
        return loader instanceof LegacyLoadAndBuild l ? l.method_33176(resources) : super_method_33176(loader, resources);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map super_method_33176(class_3503 loader, class_3300 resources) {
        return loader.method_18242(loader.method_33174(resources));
    }

    /** TagEntry.Lookup.element(id): looked up, not required */
    public static Object method_43948(class_3497.class_7474 lookup, class_2960 id) {
        return lookup.method_43948(id, false);
    }

    /** TagNetworkSerialization.NetworkPayload.size(): the number of tags sent */
    public static int method_56531(net.minecraft.class_6864.class_5748 payload) {
        return payload.method_64601();
    }

    /**
     * NetworkPayload.applyToRegistry(registry): the sent tags bound on the registry at once, as 1.21.1 did -- on
     * 1.21.2 resolved against the registry (resolve), prepared and applied (MappedRegistry.prepareTagReload). Only a
     * MappedRegistry takes tags on 1.21.2; any other registry is refused.
     */
    public static <T> void method_56532(net.minecraft.class_6864.class_5748 payload, net.minecraft.class_2378<T> registry) {
        if (!(registry instanceof net.minecraft.class_2370<T> mapped)) {
            throw new IllegalArgumentException("1.21.2 binds network tags only on a MappedRegistry: " + registry);
        }
        mapped.method_62683(payload.method_61316(registry)).method_62696();
    }

    private CbItemTags() {
    }
}
