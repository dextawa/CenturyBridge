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

    /**
     * CommonListenerCookie.strictErrorHandling(): 1.21.1's server always sent true and 1.21.2's client always
     * disconnects on a packet error (1.21.1's behaviour for true), so the flag is true.
     */
    public static boolean strictErrorHandling(net.minecraft.class_8675 cookie) {
        return true;
    }

    /** ClientCommonPacketListenerImpl.strictErrorHandling (a protected field of 1.21.1): true, as above */
    public static boolean strictErrorHandlingField(net.minecraft.class_8673 listener) {
        return true;
    }
}
