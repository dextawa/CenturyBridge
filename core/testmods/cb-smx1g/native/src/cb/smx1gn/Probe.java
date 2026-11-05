package cb.smx1gn;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Counters of the native 26.2 hooks (outside the mixin package). Written on the render thread; the driver reads them there. */
public final class Probe {
    public static final Set<GpuBuffer> LEGACY = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
    public static final Set<Object> FREEING_BACKENDS = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
    public static volatile int created;
    public static volatile int freedInPassClose;
    public static volatile int freedElsewhere;
    public static volatile int passCloses;
    public static volatile Object currentBackend;

    private Probe() {
    }

    public static void created(GpuBuffer buffer) {
        LEGACY.add(buffer);
        created++;
    }

    /** a tracked buffer goes from open to closed: attributed to RenderPass.close() when that is on the stack */
    public static void freeing(GpuBuffer buffer) {
        if (!LEGACY.contains(buffer)) {
            return;
        }
        boolean inPassClose = false;
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if ("com.mojang.blaze3d.systems.RenderPass".equals(e.getClassName()) && "close".equals(e.getMethodName())) {
                inPassClose = true;
                break;
            }
        }
        if (inPassClose) {
            freedInPassClose++;
            Object b = currentBackend;
            if (b != null) {
                FREEING_BACKENDS.add(b);
            }
        } else {
            freedElsewhere++;
        }
    }

    public static int open() {
        synchronized (LEGACY) {
            int n = 0;
            for (GpuBuffer b : LEGACY) {
                if (!b.isClosed()) {
                    n++;
                }
            }
            return n;
        }
    }
}
