package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a 1.21.3 mod's AbstractContainerWidget (class_9017) subclass extends on 1.21.4 (superRenames). 1.21.4 made
 * AbstractContainerWidget an AbstractScrollArea: it demands contentHeight() and scrollRate(), and its inherited
 * mouseScrolled scrolls the widget and takes every wheel event. A 1.21.3 container did not scroll: its content is
 * its own height (no scroll range, no scroll bar), and the wheel goes to the child under the pointer as
 * ContainerEventHandler's default sends it.
 */
@Environment(EnvType.CLIENT)
public abstract class LegacyAbstractContainerWidget extends net.minecraft.class_9017 {

    public LegacyAbstractContainerWidget(int x, int y, int width, int height, net.minecraft.class_2561 message) {
        super(x, y, width, height, message);
    }

    /** contentHeight(): the widget's own height, so maxScrollAmount is 0 */
    @Override
    public int method_44395() {
        return this.method_25364();
    }

    /** scrollRate(): never applied (nothing to scroll); 1.21.4's ScrollableLayout container uses 10 */
    @Override
    public double method_44393() {
        return 10.0;
    }

    /** mouseScrolled: 1.21.3's AbstractContainerWidget answered with ContainerEventHandler's default */
    @Override
    public boolean method_25401(double x, double y, double scrollX, double scrollY) {
        // ContainerEventHandler.mouseScrolled's body (its default cannot be named past AbstractScrollArea's override)
        return this.method_19355(x, y).filter(child -> child.method_25401(x, y, scrollX, scrollY)).isPresent();
    }
}
