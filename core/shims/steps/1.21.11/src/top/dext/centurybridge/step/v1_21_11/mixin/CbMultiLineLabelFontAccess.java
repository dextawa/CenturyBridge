package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_327;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/** MultiLineLabel's line-holding implementation ($2, package-private): its font, which 1.21.10's render / getStyle read */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_5489$2")
public abstract class CbMultiLineLabelFontAccess implements CbTextDrawing.LabelFont {
    @Shadow
    @Final
    class_327 field_26529;

    @Override
    public class_327 cb$v1_21_11$font() {
        return this.field_26529;
    }
}
