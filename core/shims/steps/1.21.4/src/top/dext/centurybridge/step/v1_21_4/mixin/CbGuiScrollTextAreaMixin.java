package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;

/**
 * 1.21.3's AbstractScrollWidget is 1.21.4's AbstractTextAreaWidget (class_10415; the converter renames the class).
 * Its old members 1.21.4 renamed (innerPadding, totalInnerPadding, renderBorder(GuiGraphics, x, y, w, h),
 * withinContentAreaTopBottom) are not given back: a 1.21.3 mod's overrides (overrideRenames), calls (methodRenames) and
 * mixins all meet on the 1.21.4 name, which 1.21.4's own code calls. Two
 * have no 1.21.4 counterpart: scrollbarWidth() (1.21.3: 8, never read by vanilla) answers the width of
 * 1.21.4's scroll bar, and withinContentAreaPoint(x, y) answers 1.21.3's rectangle test -- and a widget that
 * overrides it decides its clicks again, as 1.21.3's mouseClicked asked it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10415.class)
public abstract class CbGuiScrollTextAreaMixin extends net.minecraft.class_7528 {

    protected CbGuiScrollTextAreaMixin() {
        super(0, 0, 0, 0, null);
    }

    /** innerPadding() */
    @Shadow
    protected abstract int method_65509();

    /** totalInnerPadding() */
    @Shadow
    protected abstract int method_65512();

    /** renderBorder(GuiGraphics, x, y, width, height) */
    @Shadow
    protected abstract void method_65511(net.minecraft.class_332 graphics, int x, int y, int width, int height);

    /** withinContentAreaTopBottom(top, bottom) */
    @Shadow
    protected abstract boolean method_65510(int top, int bottom);

    /** was scrollbarWidth() (method_53532): the scroll bar beside the box (8 in 1.21.3, 6 since 1.21.4) */
    public int method_53532() {
        return net.minecraft.class_7528.field_55258;
    }

    /** was withinContentAreaPoint(double, double) (method_44388): the point lies in the widget's box */
    protected boolean method_44388(double x, double y) {
        if (this instanceof CbGuiScroll.CbLegacyContentAreaPoint legacy && CbGuiScroll.enter(this, CbGuiScroll.CONTENT_AREA_POINT)) {
            try {
                return legacy.cb$withinContentAreaPoint(x, y);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CONTENT_AREA_POINT);
            }
        }
        return x >= (double) this.method_46426() && x < (double) (this.method_46426() + this.field_22758)
            && y >= (double) this.method_46427() && y < (double) (this.method_46427() + this.field_22759);
    }

    /**
     * 1.21.3 mouseClicked, for a widget with its own withinContentAreaPoint: a press on the scroll bar starts the
     * drag; the press is taken when it is on the bar or inside the content area as the widget defines it.
     */
    @Inject(method = "method_25402", at = @At("HEAD"), cancellable = true)
    private void cb$legacyContentAreaClick(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof CbGuiScroll.CbLegacyContentAreaPoint)) {
            return;
        }
        if (!this.field_22764) {
            cir.setReturnValue(false);
            return;
        }
        boolean within = this.method_44388(x, y);
        boolean dragging = this.method_65505(x, y, button);
        boolean onBar = this.method_44392() && x >= (double) this.method_65507() && x <= (double) (this.method_65507() + net.minecraft.class_7528.field_55258)
            && y >= (double) this.method_46427() && y < (double) this.method_55443();
        cir.setReturnValue(dragging || within || onBar);
    }
}
