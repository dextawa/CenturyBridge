package top.dext.centurybridge.step.v26_1.rt;

import java.lang.reflect.Field;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.Pointer;
import sun.misc.Unsafe;

/**
 * LWJGL 3.3.3's (1.21.11) Pointer.Default protected statics, inherited by every struct and buffer class and dropped by
 * 3.4.1 (26.1): 3.3.3 computed them in Pointer.Default's class initializer as sun.misc.Unsafe and the object field offsets
 * of Pointer.Default.address and CustomBuffer.container / mark / position / limit / capacity. 3.4.1 keeps those fields, so
 * the same calls give the same values (a mod struct subclass that wrote its address or buffer fields through Unsafe with
 * them keeps working).
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjglOffsets {
    private CbLibLwjglOffsets() {
    }

    private static final Unsafe THE_UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            THE_UNSAFE = (Unsafe) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static long offset(Class<?> owner, String name) {
        try {
            return THE_UNSAFE.objectFieldOffset(owner.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static Unsafe UNSAFE() {
        return THE_UNSAFE;
    }

    public static long ADDRESS() {
        return offset(Pointer.Default.class, "address");
    }

    public static long BUFFER_CONTAINER() {
        return offset(CustomBuffer.class, "container");
    }

    public static long BUFFER_MARK() {
        return offset(CustomBuffer.class, "mark");
    }

    public static long BUFFER_POSITION() {
        return offset(CustomBuffer.class, "position");
    }

    public static long BUFFER_LIMIT() {
        return offset(CustomBuffer.class, "limit");
    }

    public static long BUFFER_CAPACITY() {
        return offset(CustomBuffer.class, "capacity");
    }
}
