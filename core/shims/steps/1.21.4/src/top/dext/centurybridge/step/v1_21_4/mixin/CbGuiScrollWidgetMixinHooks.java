package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own AbstractWidget.mouseClicked that goes with {@link CbGuiScrollWidgetMixin} (its restored
 * 1.21.3 clicked stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor inside it):
 * mouseClicked asks the restored clicked where 1.21.3's mouseClicked asked it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_339.class)
public abstract class CbGuiScrollWidgetMixinHooks {

    /** 1.21.3 AbstractWidget.clicked's own test (as clicked computes it), for telling a mixin's answer from it */
    @Unique
    private static boolean cb$box(net.minecraft.class_339 self, double x, double y) {
        return self.field_22763 && self.field_22764
            && x >= (double) self.method_46426() && y >= (double) self.method_46427()
            && x < (double) (self.method_46426() + self.method_25368()) && y < (double) (self.method_46427() + self.method_25364());
    }

    /**
     * 1.21.4's mouseClicked stores isMouseOver's answer (the call stays vanilla's, so a mod's own mixin on it still
     * applies); the stored answer is then 1.21.3's clicked where that decides: a 1.21.3 class overriding clicked, or an
     * answer otherwise than clicked's own box test (a 1.21.3 mod's mixin on clicked). Otherwise isMouseOver's answer stands.
     */
    @ModifyVariable(method = "method_25402", at = @At("STORE"), ordinal = 0)
    private boolean cb$legacyClicked(boolean over, double x, double y, int button) {
        net.minecraft.class_339 widget = (net.minecraft.class_339) (Object) this;
        CbPrioritySplitClient.WidgetClicked restored = (CbPrioritySplitClient.WidgetClicked) widget;
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
