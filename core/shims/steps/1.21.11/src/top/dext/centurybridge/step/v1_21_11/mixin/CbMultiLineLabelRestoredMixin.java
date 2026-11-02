package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11735;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_5489;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 MultiLineLabel.render(graphics, align, x, y, lineHeight, shadow, color) (method_73212) and getStyle(align, x, y,
 * lineHeight, mouseX, mouseY) (method_72103), abstract in 1.21.10 and gone from 1.21.11 (javap both: 1.21.11 keeps only
 * visitLines, method_75816), restored as default methods of MultiLineLabel. 1.21.11's own labels ($1, $2) take the
 * defaults, 1.21.10's bodies over visitLines (rt/CbTextDrawing); a 1.21.10 label implementation declares both, which
 * override them, and gets visitLines from the abstract fill (CbTextDrawing.visitLines), so a 1.21.11 screen drawing it
 * reaches its render. 1.21.11 never calls these.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_5489.class, priority = CbVanillaReads.PRIORITY)
public interface CbMultiLineLabelRestoredMixin extends CbTextDrawing.LegacyLabel {

    /** MultiLineLabel.render(graphics, align, x, y, lineHeight, shadow, color) */
    @Override
    default int method_73212(class_332 graphics, class_11735 align, int x, int y, int lineHeight, boolean shadow, int color) {
        return CbTextDrawing.method_73212((class_5489) (Object) this, graphics, align, x, y, lineHeight, shadow, color);
    }

    /** MultiLineLabel.getStyle(align, x, y, lineHeight, mouseX, mouseY) */
    @Override
    default class_2583 method_72103(class_11735 align, int x, int y, int lineHeight, double mouseX, double mouseY) {
        return CbTextDrawing.method_72103((class_5489) (Object) this, align, x, y, lineHeight, mouseX, mouseY);
    }
}
