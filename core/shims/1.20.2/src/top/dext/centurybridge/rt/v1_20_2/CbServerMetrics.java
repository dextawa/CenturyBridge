package top.dext.centurybridge.rt.v1_20_2;

import top.dext.centurybridge.facade.v1_20_2.class_3517;

/**
 * MinecraftServer.getMetricsData() (1.20.1): the server's own tick-time ring buffer. 1.20.2
 * dropped it for pushTickLog(nanos); CbServerMetricsRecorder keeps a 1.20.1 buffer per server
 * and feeds it every tick's duration, exactly where 1.20.1 pushed the sample.
 */
public final class CbServerMetrics {

    /** server.getMetricsData(): a server class's own override when it declares one */
    public static class_3517 method_15876(net.minecraft.server.MinecraftServer self) {
        if (self instanceof CbLegacyServerMetrics own) {
            return own.method_15876();
        }
        return super_method_15876(self);
    }

    /** super.getMetricsData(): the server's tick-time buffer */
    public static class_3517 super_method_15876(net.minecraft.server.MinecraftServer self) {
        return ((CbServerMetricsHolder) self).cb$metrics();
    }

    private CbServerMetrics() {
    }
}
