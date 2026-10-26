package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * AbstractWidget.clicked(x, y) (method_25361) is gone in 1.21.4: mouseClicked asks isMouseOver instead. The old
 * method is given back with 1.21.3's answer (active, visible, and the point in the widget's box), and a 1.21.3
 * widget that overrides it (renamed to {@link CbGuiScroll.CbLegacyClicked}) decides its clicks again.
 *
 * <p>mouseClicked asks the restored clicked where 1.21.3's mouseClicked asked it (1.21.3: only AbstractWidget declared
 * and called clicked, javap), so a 1.21.3 mod's mixin on clicked changes which presses a widget takes, as it did on
 * 1.21.3. While clicked gives 1.21.3's own box test, 1.21.4's isMouseOver answer stands (1.21.4 widgets that override
 * isMouseOver, such as AbstractTextAreaWidget, keep their click area); when a mixin makes clicked answer otherwise,
 * that answer decides, as 1.21.3's mouseClicked took it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_339.class)
public abstract class CbGuiScrollWidgetMixin {

    /** was clicked(double, double) (method_25361) */
    @CbVanillaReads("CbGuiScrollWidgetMixin: mouseClicked asks it where 1.21.3 did (cb$legacyClicked); its 1.21.3 box answer leaves 1.21.4's isMouseOver standing, any other answer decides")
    protected boolean method_25361(double x, double y) {
        if (this instanceof CbGuiScroll.CbLegacyClicked legacy && CbGuiScroll.enter(this, CbGuiScroll.CLICKED)) {
            try {
                return legacy.cb$clicked(x, y);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CLICKED);
            }
        }
        // 1.21.3's own body, inline: a 1.21.3 mixin anchored in clicked finds its getX / getY / getWidth / getHeight
        // calls and active / visible reads here
        net.minecraft.class_339 self = (net.minecraft.class_339) (Object) this;
        return self.field_22763 && self.field_22764
            && x >= (double) self.method_46426() && y >= (double) self.method_46427()
            && x < (double) (self.method_46426() + self.method_25368()) && y < (double) (self.method_46427() + self.method_25364());
    }

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
     * The local is typed int: the game jar has no local variable table, and the table Mixin generates from its verifier
     * frames types a stored boolean as I (sponge-mixin 0.8.7 Locals.generateLocalVariableTable), so a boolean handler
     * finds no candidate ("(0/1) succeeded"; gate 3 models it since smixin6). Among int locals (counted from slot 0,
     * arguments included: LocalVariableDiscriminator) button is ordinal 0, the stored answer (slot 6) ordinal 1
     */
    @ModifyVariable(method = "method_25402", at = @At("STORE"), ordinal = 1)
    private int cb$legacyClicked(int over, double x, double y, int button) {
        net.minecraft.class_339 widget = (net.minecraft.class_339) (Object) this;
        if (widget instanceof CbGuiScroll.CbLegacyClicked) {
            return this.method_25361(x, y) ? 1 : 0;
        }
        boolean clicked = this.method_25361(x, y);
        if (clicked != cb$box(widget, x, y)) {
            return clicked ? 1 : 0; // a 1.21.3 mixin on clicked answered otherwise: 1.21.3's mouseClicked took that answer
        }
        return over;
    }
}
