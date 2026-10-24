package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.class_1293;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1341;
import net.minecraft.class_1887;
import net.minecraft.class_1937;
import net.minecraft.class_9722;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityBreedAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityEffectsInvoker;

/**
 * LivingEntity tail members 1.21.2 reshaped:
 * <ul>
 * <li>onEffectRemoved(effect), called by 1.21.1 once per removed effect, is 1.21.2's onEffectsRemoved(effects),
 *     called once per batch; per effect its body is the same steps (attribute modifiers removed, the removal sent
 *     to the entity's player, attributes refreshed). A mod's 1.21.1 override answers each effect of a batch; its
 *     super call and a call of the old shape remove the one effect.</li>
 * <li>activeLocationDependentEnchantments(): 1.21.1 kept one map per entity, 1.21.2 one per equipment slot. The
 *     1.21.1 view is the slots' maps merged (an enchantment active in two slots answers both slots' effects); it
 *     is a snapshot that refuses writes -- 1.21.1's vanilla wrote the map only from EnchantmentHelper, which on
 *     1.21.2 writes the slot maps.</li>
 * <li>BreedGoal.level: 1.21.2 keeps it as the ServerLevel (a goal runs on the server); a 1.21.1 read takes it as
 *     the Level it was.</li>
 * </ul>
 */
public final class CbEntityLiving {

    /** a mod class's own 1.21.1 onEffectRemoved (override hook adapter) */
    public interface LegacyEffectRemoved {
        void method_6129(class_1293 effect);
    }

    /** the super accessor the onEffectRemoved hook adds to such a class */
    public interface EffectsRemovedSuper {
        void cb$super$method_6129(Collection<class_1293> effects);
    }

    /** 1.21.1 LivingEntity.onEffectRemoved(effect), called (protected: from the entity's class) */
    public static void onEffectRemoved(class_1309 entity, class_1293 effect) {
        if (entity instanceof LegacyEffectRemoved legacy) {
            legacy.method_6129(effect);
            return;
        }
        ((CbEntityEffectsInvoker) entity).cb$onEffectsRemoved(List.of(effect));
    }

    /** 1.21.2's onEffectsRemoved(effects) of a mod class overriding 1.21.1's: the old override, per effect */
    public static void effectsRemovedBridge(LegacyEffectRemoved self, Collection<class_1293> effects) {
        for (class_1293 effect : effects) {
            self.method_6129(effect);
        }
    }

    /** super.onEffectRemoved(effect) of such an override: super's 1.21.2 body for that one effect */
    public static void superOnEffectRemoved(EffectsRemovedSuper self, class_1293 effect) {
        self.cb$super$method_6129(List.of(effect));
    }

    /** 1.21.1 LivingEntity.activeLocationDependentEnchantments(): the slots' maps merged, read-only */
    public static Map<class_1887, Set<class_9722>> activeLocationDependentEnchantments(class_1309 entity) {
        if (entity instanceof LegacyLocationEnchantments legacy) {
            return legacy.method_59926();
        }
        Map<class_1887, Set<class_9722>> merged = new Reference2ObjectArrayMap<>();
        for (class_1304 slot : class_1304.values()) {
            for (Map.Entry<class_1887, Set<class_9722>> e : entity.method_59926(slot).entrySet()) {
                merged.computeIfAbsent(e.getKey(), k -> new HashSet<>()).addAll(e.getValue());
            }
        }
        merged.replaceAll((k, v) -> Collections.unmodifiableSet(v));
        return Collections.unmodifiableMap(merged);
    }

    /** a mod class's own 1.21.1 activeLocationDependentEnchantments() (override hook adapter) */
    public interface LegacyLocationEnchantments {
        Map<class_1887, Set<class_9722>> method_59926();
    }

    /** the super accessor the activeLocationDependentEnchantments hook adds to such a class */
    public interface LocationEnchantmentsSuper {
        Map<class_1887, Set<class_9722>> cb$super$method_59926(class_1304 slot);
    }

    /**
     * 1.21.2's activeLocationDependentEnchantments(slot) of a mod class overriding 1.21.1's: the override's one map,
     * for every slot (1.21.1 tracked every slot's effects in that one map)
     */
    public static Map<class_1887, Set<class_9722>> locationEnchantmentsBridge(LegacyLocationEnchantments self, class_1304 slot) {
        return self.method_59926();
    }

    /** super.activeLocationDependentEnchantments() of such an override: super's slot maps merged, read-only */
    public static Map<class_1887, Set<class_9722>> superActiveLocationDependentEnchantments(LocationEnchantmentsSuper self) {
        Map<class_1887, Set<class_9722>> merged = new Reference2ObjectArrayMap<>();
        for (class_1304 slot : class_1304.values()) {
            for (Map.Entry<class_1887, Set<class_9722>> e : self.cb$super$method_59926(slot).entrySet()) {
                merged.computeIfAbsent(e.getKey(), k -> new HashSet<>()).addAll(e.getValue());
            }
        }
        merged.replaceAll((k, v) -> Collections.unmodifiableSet(v));
        return Collections.unmodifiableMap(merged);
    }

    /** 1.21.1 BreedGoal.level */
    public static class_1937 breedGoalLevel(class_1341 goal) {
        return ((CbEntityBreedAccess) goal).cb$level();
    }

    private CbEntityLiving() {
    }
}
