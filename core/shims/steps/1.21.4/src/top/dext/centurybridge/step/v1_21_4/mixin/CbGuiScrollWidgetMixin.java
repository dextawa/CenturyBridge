package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;

/**
 * AbstractWidget.clicked(x, y) (method_25361) is gone in 1.21.4: mouseClicked asks isMouseOver instead. The old
 * method is given back with 1.21.3's answer (active, visible, and the point in the widget's box), and a 1.21.3
 * widget that overrides it (renamed to {@link CbGuiScroll.CbLegacyClicked}) decides its clicks again.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_339.class)
public abstract class CbGuiScrollWidgetMixin {

    /** was clicked(double, double) (method_25361) */
    protected boolean method_25361(double x, double y) {
        if (this instanceof CbGuiScroll.CbLegacyClicked legacy && CbGuiScroll.enter(this, CbGuiScroll.CLICKED)) {
            try {
                return legacy.cb$clicked(x, y);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CLICKED);
            }
        }
        net.minecraft.class_339 self = (net.minecraft.class_339) (Object) this;
        return self.field_22763 && self.field_22764
            && x >= (double) self.method_46426() && y >= (double) self.method_46427()
            && x < (double) (self.method_46426() + self.method_25368()) && y < (double) (self.method_46427() + self.method_25364());
    }

    @Redirect(method = "method_25402", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_339;method_25405(DD)Z"))
    private boolean cb$legacyClicked(net.minecraft.class_339 widget, double x, double y) {
        if (widget instanceof CbGuiScroll.CbLegacyClicked) {
            return this.method_25361(x, y);
        }
        return widget.method_25405(x, y);
    }
}
