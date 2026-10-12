package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * AbstractSelectionList's 1.20.2 surface that 1.20.3's widget-based list dropped: the 1.20.2 "height"
 * (kept here; the field accessors read and write it), updateSize(width, height, y0, y1),
 * setLeftPos(x) and getScrollBottom(), each on the widget's geometry as 1.20.2 laid the list out
 * (x0 = left, x1 = right, y0 = top, y1 = bottom).
 */
@Mixin(net.minecraft.class_350.class)
public abstract class CbAbstractSelectionListMixin implements CbGui.CbLegacyListGeometry {

    /** headerHeight */
    @Shadow
    protected int field_22748;

    @Unique
    private int cb$legacyHeight = Integer.MIN_VALUE;

    @Override
    public int cb$legacyHeight() {
        return cb$legacyHeight;
    }

    @Override
    public void cb$setLegacyHeight(int height) {
        cb$legacyHeight = height;
    }

    /** was updateSize(width, height, y0, y1) (method_25323): the list spans 0..width and y0..y1 */
    public void method_25323(int width, int height, int y0, int y1) {
        ((net.minecraft.class_350<?>) (Object) this).method_55444(width, y1 - y0, 0, y0);
        cb$legacyHeight = height;
    }

    /** was setLeftPos(x) (method_25333): x0 = x, x1 = x + width */
    public void method_25333(int x) {
        ((net.minecraft.class_350<?>) (Object) this).method_46421(x);
    }

    /** was getScrollBottom() (method_35721): the scroll amount less the 1.20.2 height and the header */
    public int method_35721() {
        net.minecraft.class_350<?> self = (net.minecraft.class_350<?>) (Object) this;
        return (int) self.method_25341() - CbGui.field_22743(self) - field_22748;
    }
}
