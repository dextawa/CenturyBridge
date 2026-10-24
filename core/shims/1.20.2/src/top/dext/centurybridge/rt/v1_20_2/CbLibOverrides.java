package top.dext.centurybridge.rt.v1_20_2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A converted mod's override of a library method the newer library no longer declares: the JVM has nothing to dispatch to,
 * so a bridge that stands for the old virtual call looks the override up by name and shape on the receiver's class, between it
 * and the library class that used to declare the method (cached per class and shape).
 */
public final class CbLibOverrides {
    private CbLibOverrides() {
    }

    private record Key(Class<?> type, Class<?> base, String name, MethodType shape) {
    }

    private static final Map<Key, java.util.Optional<MethodHandle>> FOUND = new ConcurrentHashMap<>();

    /** the receiver class's override of base's old method name(shape), or null when the receiver does not override it */
    public static MethodHandle find(Object receiver, Class<?> base, String name, MethodType shape) {
        Class<?> type = receiver.getClass();
        if (type == base) {
            return null;
        }
        return FOUND.computeIfAbsent(new Key(type, base, name, shape), k -> {
            for (Class<?> c = k.type(); c != null && c != k.base(); c = c.getSuperclass()) {
                try {
                    Method m = c.getDeclaredMethod(k.name(), k.shape().parameterArray());
                    if (m.getReturnType() == k.shape().returnType() && !Modifier.isStatic(m.getModifiers())
                            && !Modifier.isAbstract(m.getModifiers())) {
                        m.setAccessible(true);
                        return java.util.Optional.of(MethodHandles.lookup().unreflect(m));
                    }
                } catch (NoSuchMethodException | IllegalAccessException | SecurityException e) {
                    // not declared here
                }
            }
            return java.util.Optional.empty();
        }).orElse(null);
    }

    /** rethrows what the override threw: unchecked as it is, a checked one through the bridge's sneaky throw */
    public static RuntimeException rethrow(Throwable t) {
        return CbLibOverrides.<RuntimeException>sneaky(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneaky(Throwable t) throws T {
        throw (T) t;
    }
}
