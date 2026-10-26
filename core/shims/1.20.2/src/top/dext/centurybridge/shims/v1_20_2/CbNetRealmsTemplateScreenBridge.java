package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4419;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * RealmsSelectWorldTemplateScreen.renderMousehoverTooltip: as CbNetRealmsPendingInvitesBridge -- the
 * 1.20.1 body for a subclass's calls and super calls; 1.20.2's render shows widget tooltips instead.
 */
@Mixin(value = class_4419.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsTemplateScreenBridge extends class_4905 {

    protected CbNetRealmsTemplateScreenBridge() {
        super(null);
    }

    /** was renderMousehoverTooltip(graphics, text, mouseX, mouseY) */
    protected void method_21424(class_332 graphics, class_2561 text, int mouseX, int mouseY) {
        if (text == null) {
            return;
        }
        int x = mouseX + 12;
        int y = mouseY - 12;
        int width = this.field_22793.method_27525(text);
        graphics.method_25296(x - 3, y - 3, x + width + 3, y + 8 + 3, -1073741824, -1073741824);
        graphics.method_27535(this.field_22793, text, x, y, 16777215);
    }
}
