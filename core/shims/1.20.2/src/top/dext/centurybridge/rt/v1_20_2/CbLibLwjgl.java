package top.dext.centurybridge.rt.v1_20_2;

import static org.lwjgl.system.MemoryUtil.*;

import java.lang.ref.Cleaner;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;
import org.lwjgl.system.linux.IOVec;
import org.lwjgl.system.linux.Sockaddr;
import org.lwjgl.system.linux.liburing.IOURing;
import org.lwjgl.system.linux.liburing.IOURingCQ;
import org.lwjgl.system.linux.liburing.IOURingCQE;
import org.lwjgl.system.linux.liburing.IOURingSQ;
import org.lwjgl.system.linux.liburing.IOURingSQE;
import org.lwjgl.system.linux.liburing.LibURing;

/**
 * LWJGL 3.3.1 (1.20.1) -> 3.3.2 (1.20.2) members outside org.lwjgl.system.JNI (CbLibJni) that a mod can reach and 3.3.2 dropped
 * or reshaped, each with 3.3.1's semantics read off its bytecode (core/segments/damage-lib/1.20.1__1.20.2.tsv).
 * <ul>
 * <li>liburing (2.1 -> 2.4-dev): the struct setters 3.3.1 composed from field setters are the same field setters (3.3.2's
 * {@code set} also writes the fields 2.4 added); io_uring's three padding ints after {@code features} are the same memory;
 * the int[] / long[] overloads 3.3.2 dropped pass a native copy of the array to the buffer overload, as 3.3.1's liblwjgl
 * did (see {@link #retained} for the three whose address outlives the call).</li>
 * <li>UIO.preadv2 / pwritev2: the same libc calls, made through LWJGL's libffi.</li>
 * <li>LibCStdio.sprintf: the C library's sprintf.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjgl {
    private CbLibLwjgl() {
    }

    // ------------------------------------------------------------------ liburing struct setters (3.3.1's bodies)

    /** IOURing.set(sq, cq, flags, ring_fd, features): 3.3.1 set those five fields */
    public static IOURing set(IOURing self, IOURingSQ sq, IOURingCQ cq, int flags, int ringFd, int features) {
        return self.sq(sq).cq(cq).flags(flags).ring_fd(ringFd).features(features);
    }

    /** IOURingSQ.set(...): 3.3.1 set khead .. sqe_tail and ring_ptr */
    public static IOURingSQ set(IOURingSQ self, IntBuffer khead, IntBuffer ktail, IntBuffer kringMask, IntBuffer kringEntries,
            IntBuffer kflags, IntBuffer kdropped, IntBuffer array, IOURingSQE sqes, int sqeHead, int sqeTail, java.nio.ByteBuffer ringPtr) {
        return self.khead(khead).ktail(ktail).kring_mask(kringMask).kring_entries(kringEntries).kflags(kflags).kdropped(kdropped)
            .array(array).sqes(sqes).sqe_head(sqeHead).sqe_tail(sqeTail).ring_ptr(ringPtr);
    }

    /** IOURingCQ.set(...): 3.3.1 set khead .. cqes and ring_ptr */
    public static IOURingCQ set(IOURingCQ self, IntBuffer khead, IntBuffer ktail, IntBuffer kringMask, IntBuffer kringEntries,
            IntBuffer kflags, IntBuffer koverflow, IOURingCQE cqes, java.nio.ByteBuffer ringPtr) {
        return self.khead(khead).ktail(ktail).kring_mask(kringMask).kring_entries(kringEntries).kflags(kflags).koverflow(koverflow)
            .cqes(cqes).ring_ptr(ringPtr);
    }

    /** IOURingCQE.set(user_data, res, flags): 3.3.1 set those three fields */
    public static IOURingCQE set(IOURingCQE self, long userData, int res, int flags) {
        return self.user_data(userData).res(res).flags(flags);
    }

    /** where 3.3.1's io_uring pad[3] (three ints right after features) lies in the same struct */
    private static long ringPad(long struct) {
        return struct + IOURing.FEATURES + 4;
    }

    /** IOURing.npad(struct): 3.3.1's view of pad[3] */
    public static IntBuffer npad(long struct) {
        return memIntBuffer(ringPad(struct), 3);
    }

    /** IOURing.npad(struct, index) */
    public static int npad(long struct, int index) {
        return memGetInt(ringPad(struct) + Checks.check(index, 3) * 4);
    }

    /** IOURing.npad(struct, value) */
    public static void npad(long struct, IntBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 3);
        }
        memCopy(memAddress(value), ringPad(struct), value.remaining() * 4L);
    }

    /** IOURing.npad(struct, index, value) */
    public static void npad(long struct, int index, int value) {
        memPutInt(ringPad(struct) + Checks.check(index, 3) * 4, value);
    }

    // ------------------------------------------------------------------ liburing array overloads

    /**
     * the array's contents in native memory for one call, written back after it: 3.3.1's liblwjgl did the same
     * (GetIntArrayElements / GetLongArrayElements, a C-heap copy on HotSpot, then a mode-0 Release that copies back and frees)
     */
    private static long copyIn(MemoryStack st, int[] a) {
        long p = st.nmalloc(4, Math.max(1, a.length) * 4);
        for (int i = 0; i < a.length; i++) {
            memPutInt(p + i * 4L, a[i]);
        }
        return p;
    }

    private static void copyBack(long p, int[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = memGetInt(p + i * 4L);
        }
    }

    private static long copyIn(MemoryStack st, long[] a) {
        long p = st.nmalloc(8, Math.max(1, a.length) * 8);
        for (int i = 0; i < a.length; i++) {
            memPutLong(p + i * 8L, a[i]);
        }
        return p;
    }

    private static void copyBack(long p, long[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = memGetLong(p + i * 8L);
        }
    }

    private static final Cleaner CLEANER = Cleaner.create();

    /**
     * An array whose address the submission queue entry keeps past the call (prep_accept / prep_accept_direct's addrlen,
     * prep_files_update's fds; the kernel reads them at submission and writes addrlen at completion).
     * <p>
     * What 3.3.1 did, read off liblwjgl.so 3.3.1 (linux x64; objdump of Java_..._LibURing_nio_1uring_1prep_1accept__JIJ_3II,
     * ..._prep_1accept_1direct__JIJ_3III, ..._prep_1files_1update__J_3III): each calls JNIEnv slot 187
     * GetIntArrayElements (call *0x5d8), stores the returned pointer in the SQE, then tail-calls slot 195
     * ReleaseIntArrayElements (*0x618) with mode 0. HotSpot's GetIntArrayElements always hands back a C-heap copy, and
     * mode-0 release copies it back and frees it, so 3.3.1's SQE kept a temporary address, freed before the call returned:
     * the kernel read freed memory at submission, and its later addrlen write never reached the mod's array.
     * <p>
     * Here: native memory holding the array's contents as of the call, kept alive until the array is collected. What the mod
     * observes matches 3.3.1 (the kernel sees the contents at prep time; late kernel writes do not reach the array). The one
     * difference is that the address stays valid, where 3.3.1's pointed to freed memory (undefined behaviour, not V
     * semantics to reproduce).
     */
    private static long retained(int[] a) {
        long p = nmemAlloc(Math.max(1, a.length) * 4L);
        for (int i = 0; i < a.length; i++) {
            memPutInt(p + i * 4L, a[i]);
        }
        CLEANER.register(a, () -> nmemFree(p));
        return p;
    }

    public static int nio_uring_register_buffers_tags(long ring, long iovecs, long[] tags, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long p = copyIn(st, tags);
            int r = LibURing.nio_uring_register_buffers_tags(ring, iovecs, p, nr);
            copyBack(p, tags);
            return r;
        }
    }

    public static int io_uring_register_buffers_tags(IOURing ring, IOVec.Buffer iovecs, long[] tags) {
        if (Checks.CHECKS) {
            Checks.check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_tags(ring.address(), iovecs.address(), tags, iovecs.remaining());
    }

    public static int nio_uring_register_buffers_update_tag(long ring, int off, long iovecs, long[] tags, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long p = copyIn(st, tags);
            int r = LibURing.nio_uring_register_buffers_update_tag(ring, off, iovecs, p, nr);
            copyBack(p, tags);
            return r;
        }
    }

    public static int io_uring_register_buffers_update_tag(IOURing ring, int off, IOVec.Buffer iovecs, long[] tags) {
        if (Checks.CHECKS) {
            Checks.check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_update_tag(ring.address(), off, iovecs.address(), tags, iovecs.remaining());
    }

    public static int nio_uring_register_files(long ring, int[] files, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long p = copyIn(st, files);
            int r = LibURing.nio_uring_register_files(ring, p, nr);
            copyBack(p, files);
            return r;
        }
    }

    public static int io_uring_register_files(IOURing ring, int[] files) {
        return nio_uring_register_files(ring.address(), files, files.length);
    }

    public static int nio_uring_register_files_tags(long ring, int[] files, long[] tags, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long pf = copyIn(st, files);
            long pt = copyIn(st, tags);
            int r = LibURing.nio_uring_register_files_tags(ring, pf, pt, nr);
            copyBack(pf, files);
            copyBack(pt, tags);
            return r;
        }
    }

    public static int io_uring_register_files_tags(IOURing ring, int[] files, long[] tags) {
        if (Checks.CHECKS) {
            Checks.check(tags, files.length);
        }
        return nio_uring_register_files_tags(ring.address(), files, tags, files.length);
    }

    public static int nio_uring_register_files_update(long ring, int off, int[] files, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long p = copyIn(st, files);
            int r = LibURing.nio_uring_register_files_update(ring, off, p, nr);
            copyBack(p, files);
            return r;
        }
    }

    public static int io_uring_register_files_update(IOURing ring, int off, int[] files) {
        return nio_uring_register_files_update(ring.address(), off, files, files.length);
    }

    public static int nio_uring_register_files_update_tag(long ring, int off, int[] files, long[] tags, int nr) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long pf = copyIn(st, files);
            long pt = copyIn(st, tags);
            int r = LibURing.nio_uring_register_files_update_tag(ring, off, pf, pt, nr);
            copyBack(pf, files);
            copyBack(pt, tags);
            return r;
        }
    }

    public static int io_uring_register_files_update_tag(IOURing ring, int off, int[] files, long[] tags) {
        if (Checks.CHECKS) {
            Checks.check(tags, files.length);
        }
        return nio_uring_register_files_update_tag(ring.address(), off, files, tags, files.length);
    }

    /** values[2] is in/out (the bounded and unbounded worker limits; the previous limits come back) */
    public static int nio_uring_register_iowq_max_workers(long ring, int[] values) {
        try (MemoryStack st = MemoryStack.stackPush()) {
            long p = copyIn(st, values);
            int r = LibURing.nio_uring_register_iowq_max_workers(ring, p);
            copyBack(p, values);
            return r;
        }
    }

    public static int io_uring_register_iowq_max_workers(IOURing ring, int[] values) {
        if (Checks.CHECKS) {
            Checks.check(values, 2);
        }
        return nio_uring_register_iowq_max_workers(ring.address(), values);
    }

    public static void nio_uring_prep_accept(long sqe, int fd, long addr, int[] addrlen, int flags) {
        LibURing.nio_uring_prep_accept(sqe, fd, addr, retained(addrlen), flags);
    }

    public static void io_uring_prep_accept(IOURingSQE sqe, int fd, Sockaddr addr, int[] addrlen, int flags) {
        if (Checks.CHECKS) {
            Checks.check(addrlen, 1);
        }
        nio_uring_prep_accept(sqe.address(), fd, addr.address(), addrlen, flags);
    }

    public static void nio_uring_prep_accept_direct(long sqe, int fd, long addr, int[] addrlen, int flags, int fileIndex) {
        LibURing.nio_uring_prep_accept_direct(sqe, fd, addr, retained(addrlen), flags, fileIndex);
    }

    public static void io_uring_prep_accept_direct(IOURingSQE sqe, int fd, Sockaddr addr, int[] addrlen, int flags, int fileIndex) {
        if (Checks.CHECKS) {
            Checks.check(addrlen, 1);
        }
        nio_uring_prep_accept_direct(sqe.address(), fd, addr.address(), addrlen, flags, fileIndex);
    }

    public static void nio_uring_prep_files_update(long sqe, int[] fds, int nrFds, int offset) {
        LibURing.nio_uring_prep_files_update(sqe, retained(fds), nrFds, offset);
    }

    public static void io_uring_prep_files_update(IOURingSQE sqe, int[] fds, int offset) {
        nio_uring_prep_files_update(sqe.address(), fds, fds.length, offset);
    }

    // ------------------------------------------------------------------ UIO.preadv2 / pwritev2

    private static volatile long preadv2Cif, preadv2Fn, pwritev2Fn;

    private static long libc(String name) {
        long fn = org.lwjgl.system.linux.DynamicLinkLoader.dlsym(0L /* RTLD_DEFAULT */, name);
        if (fn == NULL) {
            throw new UnsupportedOperationException(name + " is not in this C library (glibc 2.26+ has it)");
        }
        return fn;
    }

    /** ssize_t (int fd, const struct iovec *iov, int iovcnt, off_t offset, int flags), one call interface for both */
    private static long rwCif() {
        long cif = preadv2Cif;
        if (cif == NULL) {
            synchronized (CbLibLwjgl.class) {
                cif = preadv2Cif;
                if (cif == NULL) {
                    cif = nmemCalloc(1, FFICIF.SIZEOF);
                    long types = nmemCalloc(5, Pointer.POINTER_SIZE);
                    memPutAddress(types, LibFFI.ffi_type_sint32.address());
                    memPutAddress(types + Pointer.POINTER_SIZE, LibFFI.ffi_type_pointer.address());
                    memPutAddress(types + 2L * Pointer.POINTER_SIZE, LibFFI.ffi_type_sint32.address());
                    memPutAddress(types + 3L * Pointer.POINTER_SIZE, LibFFI.ffi_type_sint64.address());
                    memPutAddress(types + 4L * Pointer.POINTER_SIZE, LibFFI.ffi_type_sint32.address());
                    int status = LibFFI.nffi_prep_cif(cif, LibFFI.FFI_DEFAULT_ABI, 5, LibFFI.ffi_type_sint64.address(), types);
                    if (status != LibFFI.FFI_OK) {
                        throw new IllegalStateException("libffi could not prepare preadv2/pwritev2: " + status);
                    }
                    preadv2Cif = cif;
                }
            }
        }
        return cif;
    }

    private static long rw(long fn, int fd, long iov, int iovcnt, long offset, int flags) {
        long cif = rwCif();
        try (MemoryStack st = MemoryStack.stackPush()) {
            long values = st.nmalloc(8, 5 * 8);
            long slots = st.nmalloc(8, 5 * 8);
            memPutInt(slots, fd);
            memPutAddress(slots + 8, iov);
            memPutInt(slots + 16, iovcnt);
            memPutLong(slots + 24, offset);
            memPutInt(slots + 32, flags);
            for (int i = 0; i < 5; i++) {
                memPutAddress(values + i * 8L, slots + i * 8L);
            }
            long r = st.nmalloc(8, 16);
            LibFFI.nffi_call(cif, fn, r, values);
            return memGetLong(r);
        }
    }

    public static long npreadv2(int fd, long iov, int iovcnt, long offset, int flags) {
        long fn = preadv2Fn;
        if (fn == NULL) {
            preadv2Fn = fn = libc("preadv2");
        }
        return rw(fn, fd, iov, iovcnt, offset, flags);
    }

    public static long preadv2(int fd, IOVec iov, int iovcnt, long offset, int flags) {
        return npreadv2(fd, iov.address(), iovcnt, offset, flags);
    }

    public static long npwritev2(int fd, long iov, int iovcnt, long offset, int flags) {
        long fn = pwritev2Fn;
        if (fn == NULL) {
            pwritev2Fn = fn = libc("pwritev2");
        }
        return rw(fn, fd, iov, iovcnt, offset, flags);
    }

    public static long pwritev2(int fd, IOVec iov, int iovcnt, long offset, int flags) {
        return npwritev2(fd, iov.address(), iovcnt, offset, flags);
    }

    // ------------------------------------------------------------------ fields

    private static volatile long sprintf;

    /**
     * LibCStdio.sprintf: the address of the C library's sprintf (3.3.1 took it from liblwjgl, linked against the platform C
     * library: glibc / libSystem on Linux / macOS, the Windows C runtime, whose sprintf msvcrt.dll exports)
     */
    public static long sprintf() {
        long fn = sprintf;
        if (fn == NULL) {
            fn = switch (Platform.get()) {
                case LINUX -> org.lwjgl.system.linux.DynamicLinkLoader.dlsym(0L, "sprintf");
                case MACOSX -> org.lwjgl.system.macosx.DynamicLinkLoader.dlsym(org.lwjgl.system.macosx.DynamicLinkLoader.RTLD_DEFAULT, "sprintf");
                case WINDOWS -> org.lwjgl.system.windows.WinBase.GetProcAddress(org.lwjgl.system.windows.WinBase.LoadLibrary("msvcrt.dll"), "sprintf");
                default -> NULL;
            };
            if (fn == NULL) {
                throw new UnsatisfiedLinkError("sprintf is not in this platform's C library");
            }
            sprintf = fn;
        }
        return fn;
    }

    private static volatile Configuration<String> shadercSpvcLibraryName;

    /**
     * Configuration.SHADERC_SPVC_LIBRARY_NAME: 3.3.1's option "org.lwjgl.shaderc.spvc.libname" (a string, Configuration's
     * package-private constructor, read by lwjgl-shaderc's SPVC binding, which 3.3.2 no longer has)
     */
    @SuppressWarnings("unchecked")
    public static Configuration<String> SHADERC_SPVC_LIBRARY_NAME() {
        Configuration<String> c = shadercSpvcLibraryName;
        if (c == null) {
            synchronized (CbLibLwjgl.class) {
                c = shadercSpvcLibraryName;
                if (c == null) {
                    try {
                        Class<?> stateInit = Class.forName("org.lwjgl.system.Configuration$StateInit");
                        Object string = stateInit.getField("STRING").get(null);
                        var ctor = Configuration.class.getDeclaredConstructor(String.class, stateInit);
                        ctor.setAccessible(true);
                        c = (Configuration<String>) ctor.newInstance("org.lwjgl.shaderc.spvc.libname", string);
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException("LWJGL Configuration", e);
                    }
                    shadercSpvcLibraryName = c;
                }
            }
        }
        return c;
    }

    /**
     * ALCapabilities.OpenAL_SOFT_bformat_ex: 3.3.1 set it from its extension set containing "OpenAL_SOFT_bformat_ex", a name
     * no OpenAL implementation reports (the extension is AL_SOFT_bformat_ex, and AL.createCapabilities adds only
     * "OpenAL10".."OpenAL11"-style version names), so it was false on every context; 3.3.2 replaced it with AL_SOFT_bformat_ex
     */
    public static boolean OpenAL_SOFT_bformat_ex(ALCapabilities caps) {
        return false;
    }
}
