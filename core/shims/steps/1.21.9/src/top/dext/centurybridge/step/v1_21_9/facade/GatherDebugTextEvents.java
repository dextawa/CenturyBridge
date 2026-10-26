package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import top.dext.centurybridge.step.v1_21_9.rt.CbDebugText;

/**
 * Fabric API 0.136's (1.21.8) GatherDebugTextEvents, deleted by Fabric API for 1.21.9: LEFT and RIGHT hand every
 * listener the F3 overlay's left / right line list to append to. 1.21.8 built each list in DebugHud.getLeftText /
 * getRightText and 0.136 fired at their return; 1.21.9 builds both lists from its debug entries inside DebugHud.render,
 * and the layer's CbDebugTextMixin fires the events on each list as it goes to be drawn (after vanilla's lines, as
 * 0.136). Until a converted mod touches this class the overlay is 1.21.9's.
 */
@Environment(EnvType.CLIENT)
public final class GatherDebugTextEvents {

    private GatherDebugTextEvents() {
    }

    public static final Event<Left> LEFT = EventFactory.createArrayBacked(Left.class, lines -> { }, callbacks -> lines -> {
        for (Left callback : callbacks) {
            callback.onGatherLeftDebugText(lines);
        }
    });

    public static final Event<Right> RIGHT = EventFactory.createArrayBacked(Right.class, lines -> { }, callbacks -> lines -> {
        for (Right callback : callbacks) {
            callback.onGatherRightDebugText(lines);
        }
    });

    static {
        CbDebugText.cbActivate();
    }

    @FunctionalInterface
    public interface Left {
        void onGatherLeftDebugText(List<String> lines);
    }

    @FunctionalInterface
    public interface Right {
        void onGatherRightDebugText(List<String> lines);
    }
}
