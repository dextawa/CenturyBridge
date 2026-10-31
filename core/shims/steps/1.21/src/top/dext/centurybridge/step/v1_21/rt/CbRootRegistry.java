package top.dext.centurybridge.step.v1_21.rt;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_2378;
import net.minecraft.class_2960;

/**
 * 1.20.6's root registry (BuiltInRegistries.REGISTRY, class_7923.field_41167) held the enchantment registry under
 * minecraft:enchantment (javap 1.20.6 class_7923.&lt;clinit&gt;: field_41176 = registerSimple(Registries.ENCHANTMENT, ..), entered
 * into the root like every built-in registry). 1.21 made enchantment a datapack registry: it is in no built-in registry and
 * the root answers that name with nothing (javap 1.21 class_7923: no field_41176, no Registries.ENCHANTMENT
 * registration). A 1.20.6 mod reaching the enchantment registry by name through the root (Kilt's VanillaForgeRegistry:
 * REGISTRY.getOptional(minecraft:enchantment), E9) then took another path.
 *
 * <p>A converted mod's root-registry lookups by name (instanceRedirects of class_2378 getOptional / get / containsKey /
 * keySet) answer minecraft:enchantment with the stand-in BuiltInRegistries.ENCHANTMENT already reads as
 * (CbEnchantments.legacy(), the fieldGetter of field_41176): the same object either way. Any other receiver, and any other
 * name on the root, is vanilla's own answer. Native code is not rewritten and keeps seeing 1.21's root.
 */
public final class CbRootRegistry {

    private CbRootRegistry() {
    }

    private static boolean root(class_2378<?> self) {
        return self == net.minecraft.class_7923.field_41167;
    }

    private static boolean enchantment(class_2960 id) {
        return net.minecraft.class_7924.field_41265.method_29177().equals(id);
    }

    /** getOptional(id) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_17966(class_2378 self, class_2960 id) {
        if (root(self) && enchantment(id)) {
            return Optional.of(CbEnchantments.legacy());
        }
        return self.method_17966(id);
    }

    /** get(id) */
    public static Object method_10223(class_2378<?> self, class_2960 id) {
        if (root(self) && enchantment(id)) {
            return CbEnchantments.legacy();
        }
        return self.method_10223(id);
    }

    /** containsKey(id) */
    public static boolean method_10250(class_2378<?> self, class_2960 id) {
        if (root(self) && enchantment(id)) {
            return true;
        }
        return self.method_10250(id);
    }

    /** keySet(): the root's names with minecraft:enchantment, as 1.20.6's root listed it */
    public static Set<class_2960> method_10235(class_2378<?> self) {
        Set<class_2960> keys = self.method_10235();
        if (!root(self)) {
            return keys;
        }
        Set<class_2960> out = new HashSet<>(keys);
        out.add(net.minecraft.class_7924.field_41265.method_29177());
        return java.util.Collections.unmodifiableSet(out);
    }
}
