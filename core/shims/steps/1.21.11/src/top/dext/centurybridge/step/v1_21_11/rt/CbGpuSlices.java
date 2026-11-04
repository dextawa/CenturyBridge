package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * GPU buffer offsets and lengths went from int to long at 1.21.11 (GpuBufferSlice, GpuBuffer.slice,
 * BufferStorage.mapBuffer, DirectStateAccess.bufferSubData). A 1.21.10 int widens losslessly; a 1.21.11 long is
 * answered to a 1.21.10 caller only when it fits, never truncated.
 */
@Environment(EnvType.CLIENT)
public final class CbGpuSlices {

    private CbGpuSlices() {
    }

    public static long widen(int value) {
        return value;
    }

    public static int narrow(long value) {
        return Math.toIntExact(value);
    }
}
