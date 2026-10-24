package top.dext.centurybridge.step.v1_20_5.rt;

import static org.lwjgl.system.MemoryUtil.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.Struct;
import org.lwjgl.system.macosx.MacOSXLibraryBundle;
import sun.misc.Unsafe;

/**
 * LWJGL 3.3.2 (1.20.4) members 3.3.3 (1.20.5) dropped, with 3.3.2's bodies (read off its source and bytecode):
 * <ul>
 * <li>Struct's and Pointer.Default's protected static {@code wrap} factories, which every 3.3.2 struct and buffer class
 *     (a mod's own included) made its instances with: {@code Unsafe.allocateInstance} of the class, then its address,
 *     container and buffer bounds written into the fields 3.3.3 still has;</li>
 * <li>Struct.create(long, ByteBuffer), abstract in 3.3.3 (its StructBuffer makes elements through it): 3.3.2's
 *     StructBuffer made them through Struct's package-private wrap(address, index, container) -- an instance of the
 *     factory's own class at that address -- which {@link #create} is;</li>
 * <li>GLFWNativeNSGL.setFramework / setFrameworkLWJGL: 3.3.2 wrote the OpenGL framework path into GLFW's
 *     {@code _glfw_opengl_library} override symbol, logging and doing nothing when the GLFW build has no such symbol (as
 *     3.3.3's does not) or the OpenGL provider is not a framework.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjgl {
    private CbLibLwjgl() {
    }

    private static final Unsafe UNSAFE;
    private static final long ADDRESS, CONTAINER, BUFFER_CONTAINER, BUFFER_MARK, BUFFER_LIMIT, BUFFER_CAPACITY;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            ADDRESS = UNSAFE.objectFieldOffset(Pointer.Default.class.getDeclaredField("address"));
            CONTAINER = UNSAFE.objectFieldOffset(Struct.class.getDeclaredField("container"));
            BUFFER_CONTAINER = UNSAFE.objectFieldOffset(CustomBuffer.class.getDeclaredField("container"));
            BUFFER_MARK = UNSAFE.objectFieldOffset(CustomBuffer.class.getDeclaredField("mark"));
            BUFFER_LIMIT = UNSAFE.objectFieldOffset(CustomBuffer.class.getDeclaredField("limit"));
            BUFFER_CAPACITY = UNSAFE.objectFieldOffset(CustomBuffer.class.getDeclaredField("capacity"));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static <T> T allocate(Class<T> clazz) {
        try {
            return clazz.cast(UNSAFE.allocateInstance(clazz));
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    // ---- Struct

    /** Struct.wrap(Class, long) */
    public static Struct wrap(Class<?> clazz, long address) {
        Struct struct = (Struct) allocate(clazz);
        UNSAFE.putLong(struct, ADDRESS, address);
        return struct;
    }

    /** Struct.wrap(Class, Struct): the value's address and container */
    public static Struct wrap(Class<?> clazz, Struct value) {
        return wrap(clazz, value.address(), (ByteBuffer) UNSAFE.getObject(value, CONTAINER));
    }

    /** Struct.wrap(Class, long, ByteBuffer) */
    public static Struct wrap(Class<?> clazz, long address, ByteBuffer container) {
        Struct struct = (Struct) allocate(clazz);
        UNSAFE.putLong(struct, ADDRESS, address);
        UNSAFE.putObject(struct, CONTAINER, container);
        return struct;
    }

    /** Struct.create(long, ByteBuffer) for a 3.3.2 struct class: 3.3.2's wrap(address, index, container) of the factory */
    @SuppressWarnings("rawtypes")
    public static Struct create(Struct factory, long address, ByteBuffer container) {
        return wrap(factory.getClass(), address, container);
    }

    // ---- Pointer.Default (struct buffers and the other custom buffers)

    /** Pointer.Default.wrap(Class, long, int) */
    public static CustomBuffer<?> wrap(Class<?> clazz, long address, int capacity) {
        CustomBuffer<?> buffer = (CustomBuffer<?>) allocate(clazz);
        UNSAFE.putLong(buffer, ADDRESS, address);
        UNSAFE.putInt(buffer, BUFFER_MARK, -1);
        UNSAFE.putInt(buffer, BUFFER_LIMIT, capacity);
        UNSAFE.putInt(buffer, BUFFER_CAPACITY, capacity);
        return buffer;
    }

    /** Pointer.Default.wrap(Class, CustomBuffer): the buffer's current address, remaining elements and container */
    public static CustomBuffer<?> wrap(Class<?> clazz, CustomBuffer<?> buffer) {
        return wrap(clazz, buffer.address(), buffer.remaining(), (ByteBuffer) UNSAFE.getObject(buffer, BUFFER_CONTAINER));
    }

    /** Pointer.Default.wrap(Class, long, int, ByteBuffer) */
    public static CustomBuffer<?> wrap(Class<?> clazz, long address, int capacity, ByteBuffer container) {
        CustomBuffer<?> buffer = wrap(clazz, address, capacity);
        UNSAFE.putObject(buffer, BUFFER_CONTAINER, container);
        return buffer;
    }

    // ---- GLFWNativeNSGL

    /** GLFWNativeNSGL.setFrameworkLWJGL() */
    public static void setFrameworkLWJGL() {
        FunctionProvider fp = GL.getFunctionProvider();
        if (!(fp instanceof MacOSXLibraryBundle)) {
            APIUtil.apiLog("GLFW OpenGL path override not set: OpenGL function provider is not a framework.");
            return;
        }
        setFramework(((MacOSXLibraryBundle) fp).getName());
    }

    /** GLFWNativeNSGL.setFramework(String) */
    public static void setFramework(String path) {
        long override = GLFW.getLibrary().getFunctionAddress("_glfw_opengl_library");
        if (override == NULL) {
            APIUtil.apiLog("GLFW OpenGL path override not set: Could not resolve override symbol.");
            return;
        }
        long a = memGetAddress(override);
        if (a != NULL) {
            nmemFree(a);
        }
        memPutAddress(override, path == null ? NULL : memAddress(memUTF8(path)));
    }
}
