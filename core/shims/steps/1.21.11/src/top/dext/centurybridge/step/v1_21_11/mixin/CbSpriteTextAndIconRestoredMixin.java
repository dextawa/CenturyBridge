package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_8662;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/** 1.21.10 SpriteIconButton.TextAndIcon.renderString(graphics, font, color): the label scrolls left of the icon */
@Environment(EnvType.CLIENT)
@Mixin(value = class_8662.class_8665.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSpriteTextAndIconRestoredMixin {

    /** SpriteIconButton.TextAndIcon.renderString(graphics, font, color) */
    public void method_48589(class_332 graphics, class_327 font, int color) {
        CbTextDrawing.textAndIconString((class_8662.class_8665) (Object) this, graphics, font, color);
    }
}
