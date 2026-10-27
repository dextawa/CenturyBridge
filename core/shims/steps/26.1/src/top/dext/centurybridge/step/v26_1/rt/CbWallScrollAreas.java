package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractScrollArea;

/**
 * 1.21.11's AbstractScrollArea(x, y, width, height, message) -- and AbstractContainerWidget's / AbstractTextAreaWidget's,
 * which pass straight to it -- asked the subclass for its scroll rate through the abstract scrollRate() and drew a
 * 6-wide scrollbar (widget/scroller over widget/scroller_background, at least 32 high, sized by content, nothing drawn
 * when not scrollable; javap class_7528). 26.1's constructors take those as ScrollbarSettings;
 * AbstractScrollArea.defaultSettings(rate) is exactly 1.21.11's bar (javap: SCROLLER_SPRITE, no disabled sprite,
 * SCROLLER_BACKGROUND_SPRITE, 6, 32, rate, resizing).
 * <p>
 * The rate in the settings is read only by 26.1's AbstractScrollArea.scrollRate() (the one reader of
 * ScrollbarSettings.scrollRate in the 26.1 jar), which a 1.21.11 subclass overrides: 1.21.11 declared scrollRate()
 * abstract, so every concrete 1.21.11 subclass answers it itself, as in 1.21.11. The settings' rate is never read for
 * one: it is 0.
 */
@Environment(EnvType.CLIENT)
public final class CbWallScrollAreas {
    private CbWallScrollAreas() {
    }

    /** the ScrollbarSettings a 1.21.11 subclass's super(x, y, width, height, message) gets */
    public static AbstractScrollArea.ScrollbarSettings legacySettings() {
        return AbstractScrollArea.defaultSettings(0);
    }
}
