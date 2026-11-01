package top.dext.centurybridge.step.v1_21.rt;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_2378;
import net.minecraft.class_2960;

/**
 * 1.20.6's root registry (BuiltInRegistries.REGISTRY, class_7923.field_41167) held every built-in registry by name. 1.21
 * stopped building some of them in (enchantment, painting_variant became datapack registries; javap 1.21 class_7923: no
 * field_41176 / field_41182), so 1.21's root answers those names with nothing. A 1.20.6 mod reaching such a registry by name
 * through the root (Kilt's VanillaForgeRegistry: REGISTRY.getOptional(id) per Forge key, E9) then took another path.
 *
 * <p>A converted mod's root-registry lookups by name (instanceRedirects of class_2378 getOptional / get / containsKey /
 * keySet) answer each such name with the object the step's fieldGetter of that BuiltInRegistries field already returns. The
 * list is generated from V's and W's BuiltInRegistries and the step's wiring (CbRootRegistryGen, core/tools/
 * gen_root_givebacks.py), never typed by hand. Any other receiver, and any other name on the root, is vanilla's own answer.
 * Native code is not rewritten and keeps seeing 1.21's root.
 */
public final class CbRootRegistry {

    private CbRootRegistry() {
    }

    private static Object given(class_2378<?> self, class_2960 id) {
        return self == net.minecraft.class_7923.field_41167 ? CbRootRegistryGen.lookup(id) : null;
    }

    /** getOptional(id) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_17966(class_2378 self, class_2960 id) {
        Object g = given(self, id);
        return g != null ? Optional.of(g) : self.method_17966(id);
    }

    /** get(id) */
    public static Object method_10223(class_2378<?> self, class_2960 id) {
        Object g = given(self, id);
        return g != null ? g : self.method_10223(id);
    }

    /** containsKey(id) */
    public static boolean method_10250(class_2378<?> self, class_2960 id) {
        return given(self, id) != null || self.method_10250(id);
    }

    /** keySet(): the root's names with the given-back ones, as 1.20.6's root listed them */
    public static Set<class_2960> method_10235(class_2378<?> self) {
        Set<class_2960> keys = self.method_10235();
        if (self != net.minecraft.class_7923.field_41167) {
            return keys;
        }
        Set<class_2960> out = new HashSet<>(keys);
        out.addAll(CbRootRegistryGen.names());
        return java.util.Collections.unmodifiableSet(out);
    }
}
