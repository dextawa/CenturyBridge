package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 Window members 1.21.2 moved:
 * - setFramerateLimit / getFramerateLimit: 1.21.1's Window stored the limit the options asked for (and the game read
 *   it back to throttle); 1.21.2's FramerateLimitTracker stores the same value (setFramerateLimit) and throttles from
 *   it. getFramerateLimit answers the stored value, as 1.21.1's did (the tracker's own getter answers the throttled
 *   one).
 * - updateDisplay(): 1.21.2's updateDisplay(frameCapture) with no Tracy frame capture (1.21.1 had none; 1.21.2's
 *   Minecraft passes one only while profiling with Tracy, and the window skips it when null).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderWindow {

    /** FramerateLimitTracker's stored limit (CbRenderFramerateAccess) */
    public interface Limit {
        int cb$limit();
    }

    private CbRenderWindow() {
    }

    public static void method_15999(net.minecraft.class_1041 window, int limit) {
        net.minecraft.class_310.method_1551().method_61964().method_61938(limit);
    }

    public static int method_16000(net.minecraft.class_1041 window) {
        return ((Limit) net.minecraft.class_310.method_1551().method_61964()).cb$limit();
    }

    public static void method_15998(net.minecraft.class_1041 window) {
        window.method_15998(null);
    }
}
