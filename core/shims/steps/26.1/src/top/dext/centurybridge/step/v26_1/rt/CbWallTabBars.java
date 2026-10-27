package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;

/**
 * 1.21.11's TabNavigationBar.setWidth(width) set the width alone (javap class_8089.method_48618); 26.1's
 * updateWidth(width) also re-arranges the tabs. 1.21.11's body is kept (the layer's access widener opens width).
 */
@Environment(EnvType.CLIENT)
public final class CbWallTabBars {
    private CbWallTabBars() {
    }

    /** setWidth(width) */
    public static void setWidth(TabNavigationBar bar, int width) {
        bar.width = width;
    }
}
