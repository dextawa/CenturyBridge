package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;

/**
 * Route for 1.21.3's AbstractWidget.clicked (restored by {@link CbGuiScrollWidgetMixin}): 1.21.3's mouseClicked asked
 * clicked(x, y), 1.21.4's asks isMouseOver(x, y) (javap both). Here 1.21.4's mouseClicked still makes its own
 * isMouseOver call (virtually, so overrides and a mod's mixins on isMouseOver apply), and its answer is then 1.21.3's
 * clicked where that decides: a 1.21.3 class overriding clicked, or an answer otherwise than clicked's own box test (a
 * 1.21.3 mod's mixin on clicked). Otherwise isMouseOver's answer stands, so unmixed it is exactly 1.21.4.
 * <p>
 * Climb-safe: the handler takes only the call's own receiver and operands, never mouseClicked's arguments or locals
 * (1.21.9 made mouseClicked (MouseButtonEvent, doubleClick) and added a boolean argument, which broke the former
 * {@code @ModifyVariable(at = STORE, ordinal = 0)} with captured (x, y, button) on every climbed client). The
 * isMouseOver(double, double) call stands in mouseClicked through 26.2.
 * <p>
 * Coexistence: below a mod's priority, so a native mod's own {@code @Redirect} of this call outranks this one (Mixin
 * keeps the higher-priority redirect); the route then gives way rather than failing the mod's mixin.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_339.class, priority = 500)
public abstract class CbGuiScrollWidgetRouteMixin {

    /** 1.21.3 AbstractWidget.clicked's own test (as clicked computes it), for telling a mixin's answer from it */
    @Unique
    private static boolean cb$box(net.minecraft.class_339 self, double x, double y) {
        return self.field_22763 && self.field_22764
            && x >= (double) self.method_46426() && y >= (double) self.method_46427()
            && x < (double) (self.method_46426() + self.method_25368()) && y < (double) (self.method_46427() + self.method_25364());
    }

    @Redirect(method = "method_25402", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_339;method_25405(DD)Z"))
    private boolean cb$legacyClicked(net.minecraft.class_339 widget, double x, double y) {
        boolean over = widget.method_25405(x, y);
        CbGuiScroll.AskClicked restored = (CbGuiScroll.AskClicked) widget;
        if (widget instanceof CbGuiScroll.CbLegacyClicked) {
            return restored.cb$v1_21_4$askClicked(x, y);
        }
        boolean clicked = restored.cb$v1_21_4$askClicked(x, y);
        if (clicked != cb$box(widget, x, y)) {
            return clicked; // a 1.21.3 mixin on clicked answered otherwise: 1.21.3's mouseClicked took that answer
        }
        return over;
    }
}
