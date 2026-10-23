package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A mod's own implementation of a Fabric API method 26.1 no longer declares. A 1.21.11 call of such a method
 * becomes a call of a layer static (instanceRedirects), which must still reach the receiver's own override
 * as 1.21.11's virtual call did: the first public or protected instance method of that name and parameter
 * types declared by the receiver's class, a superclass or an interface of either, outside Minecraft and
 * Fabric API (whose classes 26.1 does not give the method).
 */
public final class CbFapiOverrides {
    private CbFapiOverrides() {
    }

    /** the override's handle typed as {@code type} (receiver first), or null when the receiver has none */
    public static MethodHandle find(Class<?> receiver, String name, MethodType type) {
        Class<?>[] params = type.dropParameterTypes(0, 1).parameterArray();
        for (Class<?> c = receiver; c != null && c != Object.class; c = c.getSuperclass()) {
            MethodHandle h = declared(c, name, params, type);
            if (h != null) {
                return h;
            }
            if (!foreign(c)) {
                for (Class<?> i : c.getInterfaces()) {
                    h = inInterface(i, name, params, type);
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    private static MethodHandle inInterface(Class<?> i, String name, Class<?>[] params, MethodType type) {
        MethodHandle h = declared(i, name, params, type);
        if (h != null) {
            return h;
        }
        for (Class<?> s : i.getInterfaces()) {
            h = inInterface(s, name, params, type);
            if (h != null) {
                return h;
            }
        }
        return null;
    }

    private static boolean foreign(Class<?> c) {
        String n = c.getName();
        return n.startsWith("net.minecraft.") || n.startsWith("net.fabricmc.fabric.") || n.startsWith("java.");
    }

    private static MethodHandle declared(Class<?> c, String name, Class<?>[] params, MethodType type) {
        if (foreign(c)) {
            return null;
        }
        Method m;
        try {
            m = c.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isPrivate(mod) || Modifier.isAbstract(mod)
                || !type.returnType().isAssignableFrom(m.getReturnType())) {
            return null;
        }
        try {
            m.setAccessible(true);
            return MethodHandles.lookup().unreflect(m).asType(type);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("cannot reach " + m, e);
        }
    }

    /** invokes a handle from {@link #find}, rethrowing what the override threw */
    public static RuntimeException rethrow(Throwable t) {
        if (t instanceof RuntimeException r) {
            throw r;
        }
        if (t instanceof Error e) {
            throw e;
        }
        throw new RuntimeException(t);
    }
}
