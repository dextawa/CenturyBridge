package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 AbstractWidget.renderScrollingString(graphics, font, inset, color) (method_49604), gone from 1.21.11 (text goes
 * through ActiveTextCollector there; javap -c -p both): 1.21.10's body, the message scrolling between x + inset and the
 * right edge less inset over the widget's height (rt/CbTextDrawing.method_52718). 1.21.11 never calls it.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_339.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAbstractWidgetTextRestoredMixin implements CbTextDrawing.WidgetText {

    /** AbstractWidget.renderScrollingString(graphics, font, inset, color) */
    protected void method_49604(class_332 graphics, class_327 font, int inset, int color) {
        class_339 w = (class_339) (Object) this;
        int left = w.method_46426() + inset;
        int right = w.method_46426() + w.method_25368() - inset;
        CbTextDrawing.method_52718(graphics, font, w.method_25369(), left, w.method_46427(), right,
            w.method_46427() + w.method_25364(), color);
    }

    @Override
    public void cb$v1_21_11$scrollingString(class_332 graphics, class_327 font, int inset, int color) {
        this.method_49604(graphics, font, inset, color);
    }
}
