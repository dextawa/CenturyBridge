package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11909;

/**
 * Client-side counterpart of {@link CbPrioritySplit}: what a client restoring layer mixin shares with the injectors
 * split off it into a default-priority {@code <name>Hooks} mixin.
 */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /**
     * CbWidgetClickRestoredMixin: runs the restored 1.21.8 mouseClicked(x, y, button) with 1.21.9's event values,
     * onClick getting the event and double-click flag 1.21.9 was given; called virtually, so an override answers
     */
    @Environment(EnvType.CLIENT)
    public interface WidgetClick {
        boolean cb$v1_21_9$routeClick(class_11909 event, boolean doubleClick);
    }
}
