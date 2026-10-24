package top.dext.centurybridge.step.v26_1.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A 1.21.11 render method 26.1 split in two (a feature renderer's render(..) became renderSolid(..) and
 * renderTranslucent(..), called at two points of the frame). The layer's mixins give the old method back on the
 * vanilla class with 1.21.11's meaning -- both halves, one after the other -- so a mod calls it, overrides it and
 * reaches it with super. When the object's class overrides the old method, vanilla's first half hands the call
 * to it (its super call runs both halves) and vanilla's second half then does nothing: the mod's override runs
 * once per frame, as 1.21.11 called it once. While the old method runs on an object the halves run as vanilla's
 * ({@link #enter}).
 */
@Environment(EnvType.CLIENT)
public final class CbWallLegacySplit {

    private static final ThreadLocal<Map<Object, Integer>> ACTIVE = ThreadLocal.withInitial(IdentityHashMap::new);

    private static final ClassValue<Map<String, Boolean>> OVERRIDES = new ClassValue<>() {
        @Override
        protected Map<String, Boolean> computeValue(Class<?> type) {
            return new ConcurrentHashMap<>();
        }
    };

    private CbWallLegacySplit() {
    }

    /** the old method starts running on o: the halves it calls are vanilla's */
    public static void enter(Object o) {
        ACTIVE.get().merge(o, 1, Integer::sum);
    }

    public static void exit(Object o) {
        Map<Object, Integer> m = ACTIVE.get();
        Integer n = m.get(o);
        if (n == null || n <= 1) {
            m.remove(o);
        } else {
            m.put(o, n - 1);
        }
    }

    /** whether vanilla's halves hand the call to o's own old method: it overrides it and is not inside it */
    public static boolean legacy(Object o, Class<?> base, String name, Class<?>... params) {
        return !ACTIVE.get().containsKey(o) && overrides(o.getClass(), base, name, params);
    }

    /** whether a class below base declares the (non-static, non-abstract) method name(params) */
    public static boolean overrides(Class<?> type, Class<?> base, String name, Class<?>... params) {
        if (type == base) {
            return false;
        }
        StringBuilder key = new StringBuilder(name);
        for (Class<?> p : params) {
            key.append(';').append(p.getName());
        }
        return OVERRIDES.get(type).computeIfAbsent(key.toString(), k -> {
            for (Class<?> c = type; c != null && c != base; c = c.getSuperclass()) {
                try {
                    int mod = c.getDeclaredMethod(name, params).getModifiers();
                    if (!java.lang.reflect.Modifier.isStatic(mod) && !java.lang.reflect.Modifier.isAbstract(mod)) {
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    // not on this class
                }
            }
            return false;
        });
    }
}
