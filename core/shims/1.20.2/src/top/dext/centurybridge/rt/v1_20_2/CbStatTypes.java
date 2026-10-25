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
 * 1.20.1's way again (CbStatTypeLegacyHooks).
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

    /**
     * Vanilla's display name read consults the restored getTranslationKey() for every stat type whose 1.20.2 name is
     * exactly what 1.20.1 computed from it (-Dcenturybridge.legacyStatNameRoute=false turns it off).
     */
    public static final boolean ROUTE = !"false".equals(System.getProperty("centurybridge.legacyStatNameRoute"));

    /**
     * Whether a stat type's 1.20.2 display name is 1.20.1's: the plain translation of "stat_type." + its registry
     * id with ':' as '.' (1.20.1's getTranslationKey() body, computed here, not through the restored member a
     * mod's mixin may change), no arguments, siblings or style. 1.20.2's Stats.makeRegistryStatType names every
     * vanilla stat type so; a name made any other way (a native mod's) keeps 1.20.2's answer.
     */
    public static boolean vanillaFormula(class_3448<?> type, class_2561 name) {
        if (name == null || !(name.method_10851() instanceof net.minecraft.class_2588 tr) || tr.method_11023().length != 0
                || !name.method_10855().isEmpty() || !name.method_10866().method_10967()) {
            return false;
        }
        String key = goldenTranslationKey(type);
        return key != null && tr.method_11022().equals(key);
    }

    /** 1.20.1 StatType.getTranslationKey()'s body, unmixable: "stat_type." + the registry id, ':' as '.'; null unregistered */
    public static String goldenTranslationKey(class_3448<?> type) {
        net.minecraft.class_2960 id = net.minecraft.class_7923.field_41193.method_10221(type);
        return id == null ? null : "stat_type." + id.toString().replace(':', '.');
    }

    /** StatType (CbStatTypeLegacy): 1.20.1 getTranslationKey(), called virtually (a converted subclass's override answers) */
    public interface Legacy {

        String method_14957();
    }

    private CbStatTypes() {
    }
}
