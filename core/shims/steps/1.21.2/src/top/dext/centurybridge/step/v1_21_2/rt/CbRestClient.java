package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Minecraft client API on 1.21.2 that moved: the frame timer getter and the profiler pie
 * chart's key handling.
 */
@Environment(EnvType.CLIENT)
public final class CbRestClient {

    private CbRestClient() {
    }

    /** Minecraft.getTimer(): 1.21.2's getDeltaTracker, the same tracker */
    public static net.minecraft.class_9779 timer(net.minecraft.class_310 mc) {
        return mc.method_61966();
    }

    /**
     * Minecraft.debugFpsMeterKeyPress(key): the profiler pie chart's key press (descend into entry key,
     * 0 = up), which 1.21.2 moved with the chart to the debug overlay -- 1.21.1's body, on the chart's
     * own results and tree path.
     */
    public static void debugFpsMeterKeyPress(net.minecraft.class_310 mc, int key) {
        mc.method_53526().method_61981().method_61987(key);
    }
}
