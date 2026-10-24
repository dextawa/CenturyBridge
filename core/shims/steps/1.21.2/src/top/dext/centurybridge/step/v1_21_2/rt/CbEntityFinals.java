package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;

/**
 * LivingEntity members 1.21.2 made final that 1.21.1 let mods override: getScale() (same body) and
 * getEquipmentSlotForItem(stack) (1.21.2 reads the EQUIPPABLE component where 1.21.1 asked Equipable). A mod's
 * 1.21.1 override is renamed (overrideRenames, cb$ prefix) and answers the final method (CbEntityFinalsMixin); its
 * super call, an INVOKESPECIAL of the final method, reaches 1.21.2's body because the object is marked as being
 * inside its own override for the duration.
 */
public final class CbEntityFinals {

    /** a mod class's renamed 1.21.1 getScale override */
    public interface LegacyScale {
        float cb$method_55693();
    }

    /** a mod class's renamed 1.21.1 getEquipmentSlotForItem override */
    public interface LegacySlot {
        class_1304 cb$method_32326(class_1799 stack);
    }

    private static final ThreadLocal<Set<Object>> IN_SCALE =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    private static final ThreadLocal<Set<Object>> IN_SLOT =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    /** from the mixin on the final getScale: the mod class's own 1.21.1 override, or null for 1.21.2's body */
    public static Float scale(class_1309 entity) {
        if (!(entity instanceof LegacyScale legacy)) {
            return null;
        }
        Set<Object> in = IN_SCALE.get();
        if (!in.add(entity)) {
            return null;
        }
        try {
            return legacy.cb$method_55693();
        } finally {
            in.remove(entity);
        }
    }

    /** from the mixin on the final getEquipmentSlotForItem: the mod class's own override, or null */
    public static class_1304 slot(class_1309 entity, class_1799 stack) {
        if (!(entity instanceof LegacySlot legacy)) {
            return null;
        }
        Set<Object> in = IN_SLOT.get();
        if (!in.add(entity)) {
            return null;
        }
        try {
            return legacy.cb$method_32326(stack);
        } finally {
            in.remove(entity);
        }
    }

    private CbEntityFinals() {
    }
}
