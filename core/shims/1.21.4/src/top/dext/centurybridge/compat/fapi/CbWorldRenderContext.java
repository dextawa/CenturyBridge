package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.class_10209;
import net.minecraft.class_3695;
import net.minecraft.class_765;

/**
 * fabric-rendering-v1 WorldRenderContext members the 1.21.x API dropped (instanceRedirects, receiver first):
 * tickDelta() became tickCounter() (1.21), profiler() moved to the thread-local Profilers.get() (1.21.2),
 * lightmapTextureManager() is reachable from the game renderer, limitTime() (the frame chunk-build deadline)
 * no longer exists -- 0 means "no budget left", which only ever throttled optional work.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbWorldRenderContext {

    public static float tickDelta(WorldRenderContext self) {
        return self.tickCounter().method_60637(true);
    }

    public static class_3695 profiler(WorldRenderContext self) {
        return class_10209.method_64146();
    }

    public static class_765 lightmapTextureManager(WorldRenderContext self) {
        return self.gameRenderer().method_22974();
    }

    public static long limitTime(WorldRenderContext self) {
        return 0L;
    }

    private CbWorldRenderContext() {
    }
}
