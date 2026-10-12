package top.dext.centurybridge.rt.v1_20_2;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_3448;

/**
 * 1.20.1 StatType display names on 1.20.2 (s1misc).
 *
 * 1.20.1's StatType(registry) named itself lazily: getDisplayName() was the translation of
 * getTranslationKey() ("stat_type." + its registry id, overridable), computed once it was asked
 * -- after registration. 1.20.2's StatType(registry, displayName) takes the name up front. A stat
 * type a converted mod constructs the 1.20.1 way is marked here; its getDisplayName() answers
 * 1.20.1's way again (CbStatTypeLegacy).
 */
public final class CbStatTypes {

    private static final Map<class_3448<?>, Boolean> LEGACY = Collections.synchronizedMap(new WeakHashMap<>());

    /** a converted subclass's super(registry), marked until StatType(registry, name) returns */
    private static final ThreadLocal<Boolean> PENDING = new ThreadLocal<>();

    /** new StatType(registry) */
    public static <T> class_3448<T> create(class_2378<T> registry) {
        class_3448<T> type = new class_3448<>(registry, class_2561.method_43473());
        LEGACY.put(type, Boolean.TRUE);
        return type;
    }

    /**
     * The display name a converted subclass's super(registry) passes to StatType(registry, name):
     * a placeholder, the real one being computed when asked (superCtorReshapes push).
     */
    public static class_2561 legacyDisplayName() {
        PENDING.set(Boolean.TRUE);
        return class_2561.method_43473();
    }

    /** StatType(registry, name) returned: the stat type a pending mark belongs to */
    public static void claim(class_3448<?> type) {
        if (PENDING.get() != null) {
            PENDING.remove();
            LEGACY.put(type, Boolean.TRUE);
        }
    }

    public static boolean isLegacy(class_3448<?> type) {
        return LEGACY.containsKey(type);
    }

    private CbStatTypes() {
    }
}
