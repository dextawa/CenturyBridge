package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyNineSlice;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiTabButtonBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_8209.class)
public abstract class CbGuiTabButtonBridgeHooks {

    private static final net.minecraft.class_2960 CB_SHEET = new net.minecraft.class_2960("textures/gui/tab_button.png");

    private static final CbGuiOverrides.Hook CB_TEXTURE_Y = new CbGuiOverrides.Hook(net.minecraft.class_8209.class, "method_49607");

    @Redirect(method = "method_48579", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_52706(Lnet/minecraft/class_2960;IIII)V"))
    private void cb$sheetRow(net.minecraft.class_332 graphics, net.minecraft.class_2960 sprite, int x, int y, int width, int height) {
        if (CB_TEXTURE_Y.in(this)) {
            ((CbLegacyNineSlice) graphics).method_48587(CB_SHEET, x, y, width, height, 2, 2, 2, 0, 130, 24, 0, ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.TabButton) (Object) this).cb$textureY());
        } else {
            graphics.method_52706(sprite, x, y, width, height);
        }
    }
}
