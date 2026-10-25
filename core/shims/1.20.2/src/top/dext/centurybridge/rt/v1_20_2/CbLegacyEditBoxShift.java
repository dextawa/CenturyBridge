package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * EditBox's 1.20.1 shiftPressed state, kept again on 1.20.2's EditBox (CbGuiEditBoxBridgeHooks): read by the
 * frameDefaults of the cursor hooks (CbFrameDefaultsClient.editBoxShift).
 */
@Environment(EnvType.CLIENT)
public interface CbLegacyEditBoxShift {

    /** Screen.hasShiftDown() as of the box's last keyPressed, false before any */
    boolean cb$legacyShiftPressed();
}
