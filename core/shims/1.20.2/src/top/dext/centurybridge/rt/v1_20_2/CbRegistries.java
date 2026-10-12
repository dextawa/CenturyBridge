package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.serialization.Lifecycle;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2385;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;

/**
 * 1.20.1 raw-id registration on 1.20.2 (s1misc).
 *
 * 1.20.1's WritableRegistry.registerMapping(rawId, key, value, lifecycle) registered an entry at a
 * given numeric id (Registry.registerMapping(registry, rawId, name, value) was its shortcut; vanilla
 * registered mob effects that way). 1.20.2 removed it from the interface, but MappedRegistry -- the
 * registry every vanilla and Fabric registry is -- still has it under the same name and body
 * (method_46744), and its register(key, value, lifecycle) is registerMapping at the next free id. A
 * converted registry implementation answering the 1.20.1 method itself is asked first; a registry
 * that is neither gets register(key, value, lifecycle), the only registration it offers.
 */
public final class CbRegistries {

    /** WritableRegistry.registerMapping(rawId, key, value, lifecycle) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6880 method_10273(class_2385 registry, int rawId, class_5321 key, Object value, Lifecycle lifecycle) {
        if (registry instanceof CbLegacyWritableRegistry legacy) {
            return legacy.method_10273(rawId, key, value, lifecycle);
        }
        if (registry instanceof class_2370 mapped) {
            return mapped.method_46744(rawId, key, value, lifecycle);
        }
        return registry.method_10272(key, value, lifecycle);
    }

    /** Registry.registerMapping(registry, rawId, name, value), 1.20.1 body */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object method_10231(class_2378 registry, int rawId, String name, Object value) {
        method_10273((class_2385) registry, rawId, class_5321.method_29179(registry.method_30517(), new class_2960(name)),
            value, Lifecycle.stable());
        return value;
    }

    private CbRegistries() {
    }
}
