package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWNativeEGL;
import org.lwjgl.glfw.GLFWNativeGLX;
import org.lwjgl.openal.SOFTCallbackBufferTypeI;
import org.lwjgl.openal.SOFTEventProcI;
import org.lwjgl.openal.SOFTEvents;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GLXAMDGPUAssociation;
import org.lwjgl.system.Callback;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibraryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.system.ThreadLocalUtil;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.windows.WindowsUtil;
import org.lwjgl.util.freetype.FT_MM_Var;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * LWJGL 3.3.3 (1.21.11) -> 3.4.1 (26.1), the remaining single members of core/segments/damage-lib/1.21.11__26.1.tsv, each
 * given 3.3.3's body (javap of the 3.3.3 jars) over 3.4.1's API. Where 3.4.1 keeps the state only privately (ThreadLocalUtil's
 * env setup, Configuration's constructor, Callback.Descriptor's CIF, SharedLibraryUtil's constructor) it is reached by
 * reflection: LWJGL is not a game class, so the layer's access widener cannot open it.
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjglMisc {
    private CbLibLwjglMisc() {
    }

    // ------------------------------------------------------------------ MathUtil (3.3.3's own arithmetic; the JDK's gives the same values)

    /** MathUtil.mathMultiplyHighU64: the high 64 bits of the unsigned 128-bit product */
    public static long mathMultiplyHighU64(long x, long y) {
        return Math.unsignedMultiplyHigh(x, y);
    }

    /** MathUtil.mathMultiplyHighS64: the high 64 bits of the signed 128-bit product */
    public static long mathMultiplyHighS64(long x, long y) {
        return Math.multiplyHigh(x, y);
    }

    /** MathUtil.mathDivideUnsigned */
    public static long mathDivideUnsigned(long dividend, long divisor) {
        return Long.divideUnsigned(dividend, divisor);
    }

    /** MathUtil.mathRemainderUnsigned */
    public static long mathRemainderUnsigned(long dividend, long divisor) {
        return Long.remainderUnsigned(dividend, divisor);
    }

    // ------------------------------------------------------------------ ThreadLocalUtil / WindowsUtil / SharedLibraryUtil

    private static final MethodHandle SETUP_ENV_DATA;
    private static final int JNI_FUNCTION_COUNT;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ThreadLocalUtil.class, MethodHandles.lookup());
            SETUP_ENV_DATA = lookup.findStatic(ThreadLocalUtil.class, "setupEnvData", MethodType.methodType(long.class, int.class));
            JNI_FUNCTION_COUNT = (int) lookup.findStaticGetter(ThreadLocalUtil.class, "JNI_NATIVE_INTERFACE_FUNCTION_COUNT", int.class).invoke();
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    /** ThreadLocalUtil.setupEnvData(): 3.3.3's nsetupEnvData(JNI_NATIVE_INTERFACE_FUNCTION_COUNT), 3.4.1's private native of it */
    public static long setupEnvData() {
        try {
            return (long) SETUP_ENV_DATA.invokeExact(JNI_FUNCTION_COUNT);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    /**
     * WindowsUtil.windowsThrowException(msg): "msg (error code = <last error>)" with 3.3.3's saved last error, which the
     * layer keeps per thread for converted calls (CbLibLwjglErrors)
     */
    public static void windowsThrowException(String msg) {
        WindowsUtil.windowsThrowException(msg, CbLibLwjglErrors.lastErrorBuffer());
    }

    /** new SharedLibraryUtil(): 3.3.3's constructor was public (the class has only statics); 3.4.1's is private */
    public static SharedLibraryUtil sharedLibraryUtil() {
        try {
            Constructor<SharedLibraryUtil> c = SharedLibraryUtil.class.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------ GLFW native access

    /** GLFWNativeEGL.glfwGetEGLConfig(window): the window's EGLConfig, NULL when GLFW has none (3.4.1 answers it out-of-band) */
    public static long glfwGetEGLConfig(long window) {
        if (Checks.CHECKS) {
            Checks.check(window);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer config = stack.callocPointer(1);
            return GLFWNativeEGL.glfwGetEGLConfig(window, config) ? config.get(0) : MemoryUtil.NULL;
        }
    }

    /** GLFWNativeGLX.glfwGetGLXFBConfig(window) */
    public static long glfwGetGLXFBConfig(long window) {
        if (Checks.CHECKS) {
            Checks.check(window);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer config = stack.callocPointer(1);
            return GLFWNativeGLX.glfwGetGLXFBConfig(window, config) ? config.get(0) : MemoryUtil.NULL;
        }
    }

    // ------------------------------------------------------------------ OpenAL / OpenGL

    /** SOFTEvents.alEventCallbackSOFT(callback, userParam): the callback's address and the buffer's (NULL for none) */
    public static void alEventCallbackSOFT(SOFTEventProcI callback, ByteBuffer userParam) {
        SOFTEvents.nalEventCallbackSOFT(callback.address(), MemoryUtil.memAddressSafe(userParam));
    }

    /** GL32C/GL32/ARBSync.nglDeleteSync(sync): 3.4.1's glDeleteSync is the native itself, unchecked as 3.3.3's n-form */
    public static void nglDeleteSync(long sync) {
        GL32C.glDeleteSync(sync);
    }

    /**
     * GLXAMDGPUAssociation.glXGetGPUIDsAMD(maxCount, ids): 3.3.3 bound the ids pointer as an int and passed it in a 32-bit
     * register (JNI.callI(II)), i.e. zero-extended; 3.4.1's pointer form gets the same value
     */
    public static int glXGetGPUIDsAMD(int maxCount, int ids) {
        return GLXAMDGPUAssociation.nglXGetGPUIDsAMD(maxCount, Integer.toUnsignedLong(ids));
    }

    /** WGL.nwglGetCurrentDC(function): the function at that address called with no argument, its pointer answered */
    public static long nwglGetCurrentDC(long function) {
        return JNI.invokeP(function);
    }

    /**
     * SOFTCallbackBufferTypeI.invoke(..) called on an instance: 3.4.1's int form (ALsizei, all OpenAL reads of 3.3.3's
     * pointer-typed return too), sign-extended
     */
    public static long invoke(SOFTCallbackBufferTypeI callback, long userptr, long sampledata, int numbytes) {
        return callback.invoke(userptr, sampledata, numbytes);
    }

    /** a 1.21.11 implementation's long answer as the ALsizei OpenAL reads: its low 32 bits */
    public static int alsizei(long value) {
        return (int) value;
    }

    /** 3.3.3's SOFTCallbackBufferTypeI shape, for lambdas written against it (wiring lambdaAdapters) */
    @FunctionalInterface
    public interface SOFTCallbackBufferTypeOld {
        long invoke(long userptr, long sampledata, int numbytes);
    }

    /** a 3.3.3-shaped lambda as 3.4.1's SOFTCallbackBufferTypeI */
    public static SOFTCallbackBufferTypeI softCallbackBufferType(SOFTCallbackBufferTypeOld old) {
        return (userptr, sampledata, numbytes) -> (int) old.invoke(userptr, sampledata, numbytes);
    }

    // ------------------------------------------------------------------ callbacks: 3.3.3's FFICIF, 3.4.1's Descriptor

    private static final Field DESCRIPTOR_CIF;

    static {
        try {
            DESCRIPTOR_CIF = Callback.Descriptor.class.getDeclaredField("cif");
            DESCRIPTOR_CIF.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** CallbackI.getCallInterface() on any callback: the CIF of its 3.4.1 descriptor (the only part of it 3.4.1's upcalls read) */
    public static FFICIF getCallInterface(CallbackI callback) {
        try {
            return (FFICIF) DESCRIPTOR_CIF.get(callback.getDescriptor());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * a 1.21.11 class implementing CallbackI itself (getCallInterface) asked for 3.4.1's descriptor: one around its CIF (3.4.1's
     * Upcalls.upcallCreate reads only the descriptor's CIF)
     */
    public static Callback.Descriptor getDescriptor(CbLibLwjglCallbackOld callback) {
        return new Callback.Descriptor(MethodHandles.lookup(), callback.getCallInterface());
    }

    /** a 1.21.11 Callback subclass's super(cif): 3.4.1's constructor takes the descriptor around it */
    public static Callback.Descriptor descriptor(FFICIF cif) {
        return new Callback.Descriptor(MethodHandles.lookup(), cif);
    }

    // ------------------------------------------------------------------ FFICIF.arg_types(count)

    /** FFICIF.narg_types(struct, count): count pointers at the struct's arg_types (3.4.1 sizes the buffer by nargs itself) */
    public static PointerBuffer narg_types(long struct, int capacity) {
        return MemoryUtil.memPointerBuffer(MemoryUtil.memGetAddress(struct + FFICIF.ARG_TYPES), capacity);
    }

    /** FFICIF.arg_types(count) */
    public static PointerBuffer arg_types(FFICIF self, int capacity) {
        return narg_types(self.address(), capacity);
    }

    /** FFICIF.Buffer.arg_types(count): the current element's */
    public static PointerBuffer arg_types(FFICIF.Buffer self, int capacity) {
        return narg_types(self.address(), capacity);
    }

    // ------------------------------------------------------------------ CustomBuffer.create (3.4.1 abstract)

    /**
     * a 1.21.11 buffer class's create(address, container, mark, position, limit, capacity), which 3.4.1 asks every buffer
     * class for: 3.3.3 made such instances itself (CustomBuffer.slice / duplicate: Unsafe.allocateInstance of the class, then
     * its address and buffer fields written), so does this
     */
    public static CustomBuffer<?> create(CustomBuffer<?> self, long address, ByteBuffer container, int mark, int position, int limit,
            int capacity) {
        sun.misc.Unsafe unsafe = CbLibLwjglOffsets.UNSAFE();
        try {
            CustomBuffer<?> buffer = (CustomBuffer<?>) unsafe.allocateInstance(self.getClass());
            unsafe.putLong(buffer, CbLibLwjglOffsets.ADDRESS(), address);
            unsafe.putObject(buffer, CbLibLwjglOffsets.BUFFER_CONTAINER(), container);
            unsafe.putInt(buffer, CbLibLwjglOffsets.BUFFER_MARK(), mark);
            unsafe.putInt(buffer, CbLibLwjglOffsets.BUFFER_POSITION(), position);
            unsafe.putInt(buffer, CbLibLwjglOffsets.BUFFER_LIMIT(), limit);
            unsafe.putInt(buffer, CbLibLwjglOffsets.BUFFER_CAPACITY(), capacity);
            return buffer;
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    // ------------------------------------------------------------------ TinyFileDialogs

    /** TinyFileDialogs.tinyfd_assumeGraphicDisplay: 3.3.3's constant (ConstantValue "tinyfd_assumeGraphicDisplay") */
    public static String tinyfd_assumeGraphicDisplay() {
        return "tinyfd_assumeGraphicDisplay";
    }

    /** tinyfd_messageBox(.., boolean defaultButton): 3.3.3 passed it as 1/0 and answered "not 0" */
    public static boolean tinyfd_messageBox(ByteBuffer title, ByteBuffer message, ByteBuffer dialogType, ByteBuffer iconType,
            boolean defaultButton) {
        return TinyFileDialogs.tinyfd_messageBox(title, message, dialogType, iconType, defaultButton ? 1 : 0) != 0;
    }

    /** tinyfd_messageBox(CharSequence.., boolean defaultButton) */
    public static boolean tinyfd_messageBox(CharSequence title, CharSequence message, CharSequence dialogType, CharSequence iconType,
            boolean defaultButton) {
        return TinyFileDialogs.tinyfd_messageBox(title, message, dialogType, iconType, defaultButton ? 1 : 0) != 0;
    }

    // ------------------------------------------------------------------ Configuration options 3.4.1 dropped with their bindings

    private static Configuration<?> option(String property, String init) {
        try {
            Class<?> stateInit = Class.forName("org.lwjgl.system.Configuration$StateInit");
            Field f = stateInit.getDeclaredField(init);
            f.setAccessible(true);
            Constructor<Configuration> c = Configuration.class.getDeclaredConstructor(String.class, stateInit);
            c.setAccessible(true);
            return c.newInstance(property, f.get(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class Options {
        static final Configuration<?> CUDA_LIBRARY_NAME = option("org.lwjgl.cuda.libname", "STRING");
        static final Configuration<?> CUDA_TOOLKIT_VERSION = option("org.lwjgl.cuda.toolkit.version", "STRING");
        static final Configuration<?> CUDA_TOOLKIT_PATH = option("org.lwjgl.cuda.toolkit.path", "STRING");
        static final Configuration<?> CUDA_NVRTC_LIBRARY_NAME = option("org.lwjgl.cuda.nvrtc.libname", "STRING");
        static final Configuration<?> CUDA_NVRTC_BUILTINS_LIBRARY_NAME = option("org.lwjgl.cuda.nvrtc-builtins.libname", "STRING");
        static final Configuration<?> CUDA_API_PER_THREAD_DEFAULT_STREAM = option("org.lwjgl.cuda.ptds", "BOOLEAN");
        static final Configuration<?> OPENVR_LIBRARY_NAME = option("org.lwjgl.openvr.libname", "STRING");
    }

    /** Configuration.CUDA_LIBRARY_NAME: 3.3.3's option ("org.lwjgl.cuda.libname", a string) */
    public static Configuration<?> CUDA_LIBRARY_NAME() {
        return Options.CUDA_LIBRARY_NAME;
    }

    /** Configuration.CUDA_TOOLKIT_VERSION ("org.lwjgl.cuda.toolkit.version") */
    public static Configuration<?> CUDA_TOOLKIT_VERSION() {
        return Options.CUDA_TOOLKIT_VERSION;
    }

    /** Configuration.CUDA_TOOLKIT_PATH ("org.lwjgl.cuda.toolkit.path") */
    public static Configuration<?> CUDA_TOOLKIT_PATH() {
        return Options.CUDA_TOOLKIT_PATH;
    }

    /** Configuration.CUDA_NVRTC_LIBRARY_NAME ("org.lwjgl.cuda.nvrtc.libname") */
    public static Configuration<?> CUDA_NVRTC_LIBRARY_NAME() {
        return Options.CUDA_NVRTC_LIBRARY_NAME;
    }

    /** Configuration.CUDA_NVRTC_BUILTINS_LIBRARY_NAME ("org.lwjgl.cuda.nvrtc-builtins.libname") */
    public static Configuration<?> CUDA_NVRTC_BUILTINS_LIBRARY_NAME() {
        return Options.CUDA_NVRTC_BUILTINS_LIBRARY_NAME;
    }

    /** Configuration.CUDA_API_PER_THREAD_DEFAULT_STREAM ("org.lwjgl.cuda.ptds", a boolean) */
    public static Configuration<?> CUDA_API_PER_THREAD_DEFAULT_STREAM() {
        return Options.CUDA_API_PER_THREAD_DEFAULT_STREAM;
    }

    /** Configuration.OPENVR_LIBRARY_NAME ("org.lwjgl.openvr.libname") */
    public static Configuration<?> OPENVR_LIBRARY_NAME() {
        return Options.OPENVR_LIBRARY_NAME;
    }

    // ------------------------------------------------------------------ FT_MM_Var allocation (3.4.1: FreeType-owned only)

    /** FT_MM_Var.malloc() */
    public static FT_MM_Var malloc() {
        return FT_MM_Var.create(MemoryUtil.nmemAllocChecked(FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.calloc() */
    public static FT_MM_Var calloc() {
        return FT_MM_Var.create(MemoryUtil.nmemCallocChecked(1, FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.create(): backed by a GC-managed buffer */
    public static FT_MM_Var create() {
        return new FT_MM_Var(BufferUtils.createByteBuffer(FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.malloc(capacity) */
    public static FT_MM_Var.Buffer malloc(int capacity) {
        return FT_MM_Var.create(MemoryUtil.nmemAllocChecked(StructAccess.checkMalloc(capacity, FT_MM_Var.SIZEOF)), capacity);
    }

    /** FT_MM_Var.calloc(capacity) */
    public static FT_MM_Var.Buffer calloc(int capacity) {
        return FT_MM_Var.create(MemoryUtil.nmemCallocChecked(capacity, FT_MM_Var.SIZEOF), capacity);
    }

    /** FT_MM_Var.create(capacity): backed by a GC-managed buffer */
    public static FT_MM_Var.Buffer create(int capacity) {
        return new FT_MM_Var.Buffer(StructAccess.create(capacity, FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.malloc(stack) */
    public static FT_MM_Var malloc(MemoryStack stack) {
        return FT_MM_Var.create(stack.nmalloc(FT_MM_Var.ALIGNOF, FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.calloc(stack) */
    public static FT_MM_Var calloc(MemoryStack stack) {
        return FT_MM_Var.create(stack.ncalloc(FT_MM_Var.ALIGNOF, 1, FT_MM_Var.SIZEOF));
    }

    /** FT_MM_Var.malloc(capacity, stack) */
    public static FT_MM_Var.Buffer malloc(int capacity, MemoryStack stack) {
        return FT_MM_Var.create(stack.nmalloc(FT_MM_Var.ALIGNOF, capacity * FT_MM_Var.SIZEOF), capacity);
    }

    /** FT_MM_Var.calloc(capacity, stack) */
    public static FT_MM_Var.Buffer calloc(int capacity, MemoryStack stack) {
        return FT_MM_Var.create(stack.ncalloc(FT_MM_Var.ALIGNOF, capacity, FT_MM_Var.SIZEOF), capacity);
    }

    /** FT_MM_Var.close() (3.3.3: NativeResource's default, free()) */
    public static void close(FT_MM_Var self) {
        self.free();
    }

    /** FT_MM_Var.Buffer.close() */
    public static void close(FT_MM_Var.Buffer self) {
        self.free();
    }

    /** Struct's protected allocation helpers, the ones 3.3.3's FT_MM_Var called (a subclass may call them) */
    private abstract static class StructAccess extends Struct<StructAccess> {
        private StructAccess() {
            super(0L, null);
        }

        static long checkMalloc(int elements, int elementSize) {
            return __checkMalloc(elements, elementSize);
        }

        static ByteBuffer create(int elements, int elementSize) {
            return __create(elements, elementSize);
        }
    }

    private static RuntimeException rethrow(Throwable t) {
        return CbLibLwjglMisc.<RuntimeException>sneaky(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneaky(Throwable t) throws T {
        throw (T) t;
    }
}
