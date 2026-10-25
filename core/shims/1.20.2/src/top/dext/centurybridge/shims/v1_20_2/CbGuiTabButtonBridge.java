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
@Mixin(value = net.minecraft.class_8209.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiTabButtonBridge implements top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.TabButton {

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

    @Override
    public int cb$textureY() {
        return this.method_49607();
    }
}
