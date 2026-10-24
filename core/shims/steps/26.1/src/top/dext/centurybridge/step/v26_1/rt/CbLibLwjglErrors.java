package top.dext.centurybridge.step.v26_1.rt;

import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

/**
 * LWJGL 3.3.3's (1.21.11) per-thread saved GetLastError() / errno, which 3.4.1 (26.1) replaced by an out-parameter on every
 * such function: one direct one-int buffer per thread for each, handed to the 3.4.1 calls a converted 3.3.3 call makes
 * (core/tools/gen_lib_lwjgl_errno.py) and read by 3.3.3's WinBase.getLastError() / LibCErrno.getErrno() -- the value the
 * last such call saved on this thread, as 3.3.3 answered (0 before any).
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjglErrors {
    private CbLibLwjglErrors() {
    }

    private static final ThreadLocal<IntBuffer> LAST_ERROR = ThreadLocal.withInitial(() -> BufferUtils.createIntBuffer(1));
    private static final ThreadLocal<IntBuffer> ERRNO = ThreadLocal.withInitial(() -> BufferUtils.createIntBuffer(1));

    public static IntBuffer lastErrorBuffer() {
        return LAST_ERROR.get();
    }

    public static long lastErrorAddress() {
        return MemoryUtil.memAddress(LAST_ERROR.get());
    }

    public static IntBuffer errnoBuffer() {
        return ERRNO.get();
    }

    public static long errnoAddress() {
        return MemoryUtil.memAddress(ERRNO.get());
    }

    /** WinBase.getLastError() */
    public static int getLastError() {
        return LAST_ERROR.get().get(0);
    }

    /** LibCErrno.getErrno() */
    public static int getErrno() {
        return ERRNO.get().get(0);
    }
}
