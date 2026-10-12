package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbGuiFurnace;

/**
 * A furnace screen built through the 1.20.1 constructor (its progress sprites are CbGuiFurnace's markers)
 * draws its flame and arrow as 1.20.1's drawBackground did: from its own sheet, the flame at (176, 12 - k)
 * k + 1 rows high and the arrow at (176, 14) k + 1 columns wide, k being 1.20.1's integer progress.
 * Screens built with real sprites (every 1.20.2 one) draw them as 1.20.2 does.
 */
@Mixin(net.minecraft.class_489.class)
public abstract class CbGuiFurnaceScreenBridge<T extends net.minecraft.class_1720> extends net.minecraft.class_465<T> {

    @Shadow
    @Final
    private net.minecraft.class_2960 field_18975;

    private CbGuiFurnaceScreenBridge() {
        super(null, null, null);
    }

    @Redirect(method = "method_2389", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_52708(Lnet/minecraft/class_2960;IIIIIIII)V"))
    private void cb$legacyProgress(net.minecraft.class_332 graphics, net.minecraft.class_2960 sprite, int spriteWidth, int spriteHeight,
                                   int u, int v, int x, int y, int width, int height) {
        if (sprite == CbGuiFurnace.LIT) {
            int k = CbGuiFurnace.cbLitProgress(this.field_2797);
            graphics.method_25302(this.field_18975, this.field_2776 + 56, this.field_2800 + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        } else if (sprite == CbGuiFurnace.BURN) {
            int k = CbGuiFurnace.cbBurnProgress(this.field_2797);
            graphics.method_25302(this.field_18975, this.field_2776 + 79, this.field_2800 + 34, 176, 14, k + 1, 16);
        } else {
            graphics.method_52708(sprite, spriteWidth, spriteHeight, u, v, x, y, width, height);
        }
    }
}
