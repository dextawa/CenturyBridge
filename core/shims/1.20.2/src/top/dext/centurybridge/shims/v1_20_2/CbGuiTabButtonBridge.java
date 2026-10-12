package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyNineSlice;

/**
 * TabButton.getTextureY(): in 1.20.1 renderWidget nine-sliced tab_button.png at the row this returned
 * (0 selected, 24 selected and hovered or focused, 48 neither, 72 hovered or focused only), so a
 * subclass could pick its own row; 1.20.2 draws a sprite pair instead and dropped it. Restored with the
 * 1.20.1 body, and a subclass that overrides it gets the 1.20.1 drawing back -- the sheet (rebuilt from
 * 1.20.2's tab sprites by CbGuiLegacyTextures) at the row it picks. Tab buttons that do not override it
 * draw as 1.20.2 does; the same art.
 */
@Mixin(net.minecraft.class_8209.class)
public abstract class CbGuiTabButtonBridge {

    private static final net.minecraft.class_2960 CB_SHEET = new net.minecraft.class_2960("textures/gui/tab_button.png");
    private static final CbGuiOverrides.Hook CB_TEXTURE_Y = new CbGuiOverrides.Hook(net.minecraft.class_8209.class, "method_49607");

    /** 1.20.1 TabButton.getTextureY() */
    protected int method_49607() {
        net.minecraft.class_8209 self = (net.minecraft.class_8209) (Object) this;
        int row = 2;
        if (self.method_49611() && self.method_25367()) {
            row = 1;
        } else if (self.method_49611()) {
            row = 0;
        } else if (self.method_25367()) {
            row = 3;
        }
        return row * 24;
    }

    @Redirect(method = "method_48579", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_52706(Lnet/minecraft/class_2960;IIII)V"))
    private void cb$sheetRow(net.minecraft.class_332 graphics, net.minecraft.class_2960 sprite, int x, int y, int width, int height) {
        if (CB_TEXTURE_Y.in(this)) {
            ((CbLegacyNineSlice) graphics).method_48587(CB_SHEET, x, y, width, height, 2, 2, 2, 0, 130, 24, 0, this.method_49607());
        } else {
            graphics.method_52706(sprite, x, y, width, height);
        }
    }
}
