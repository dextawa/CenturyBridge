package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4388;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * RealmsConfigureWorldScreen.renderMousehoverTooltip: as CbNetRealmsPendingInvitesBridge, with this
 * screen's 1.20.1 body (the box moves left of the mouse when it would pass the screen's right edge).
 */
@Mixin(value = class_4388.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsConfigureWorldBridge extends class_4905 {

    @Shadow
    private int field_19796;

    protected CbNetRealmsConfigureWorldBridge() {
        super(null);
    }

    /** was renderMousehoverTooltip(graphics, text, mouseX, mouseY) */
    protected void method_21214(class_332 graphics, class_2561 text, int mouseX, int mouseY) {
        int x = mouseX + 12;
        int y = mouseY - 12;
        int width = this.field_22793.method_27525(text);
        if (x + width + 3 > field_19796) {
            x = x - width - 20;
        }
        graphics.method_25296(x - 3, y - 3, x + width + 3, y + 8 + 3, -1073741824, -1073741824);
        graphics.method_27535(this.field_22793, text, x, y, 16777215);
    }
}
