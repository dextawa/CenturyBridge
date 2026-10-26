package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_9.facade.GatherDebugTextEvents;

/** fires the restored GatherDebugTextEvents (CbDebugTextMixin); inert until GatherDebugTextEvents initialized */
@Environment(EnvType.CLIENT)
public final class CbDebugText {

    private CbDebugText() {
    }

    private static volatile boolean active;

    public static void cbActivate() {
        active = true;
    }

    /** the overlay's line list about to be drawn on the left (left = true) or right side */
    public static List<String> cbGather(List<String> lines, boolean left) {
        if (!active) {
            return lines;
        }
        if (left) {
            GatherDebugTextEvents.LEFT.invoker().onGatherLeftDebugText(lines);
        } else {
            GatherDebugTextEvents.RIGHT.invoker().onGatherRightDebugText(lines);
        }
        return lines;
    }
}
