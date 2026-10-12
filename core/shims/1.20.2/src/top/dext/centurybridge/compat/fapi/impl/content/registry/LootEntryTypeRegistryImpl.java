package top.dext.centurybridge.compat.fapi.impl.content.registry;

import java.lang.reflect.Method;
import top.dext.centurybridge.compat.fapi.registry.LootEntryTypeRegistry;

/**
 * 1.20.1's content-registries LootEntryTypeRegistryImpl, ported as it was: it looks for a static
 * method of LootPoolEntryTypes taking just a LootPoolEntry.Serializer and registers through it.
 * Neither 1.20.1's LootPoolEntryTypes (whose only registering method, method_29317, takes a name and a
 * JsonSerializer) nor 1.20.2's has one, so in 1.20.1 this class failed its static initialisation
 * with "Could not find register-like method in LootEntries!" the first time INSTANCE was touched --
 * and the port fails the same way. Mods calling through the loot tables v1 registry
 * (loot/v1/LootEntryTypeRegistry) are what registered entry types in 1.20.1.
 */
public final class LootEntryTypeRegistryImpl implements LootEntryTypeRegistry {

    private static final Method REGISTER_METHOD;

    static {
        Method target = null;
        for (Method m : net.minecraft.class_75.class.getDeclaredMethods()) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == top.dext.centurybridge.facade.v1_20_2.class_79$class_5337.class) {
                if (target != null) {
                    throw new RuntimeException("More than one register-like method found in LootEntries!");
                }
                target = m;
            }
        }
        if (target == null) {
            throw new RuntimeException("Could not find register-like method in LootEntries!");
        }
        REGISTER_METHOD = target;
        REGISTER_METHOD.setAccessible(true);
    }

    @Override
    public void register(top.dext.centurybridge.facade.v1_20_2.class_79$class_5337<?> serializer) {
        try {
            REGISTER_METHOD.invoke(null, serializer);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
