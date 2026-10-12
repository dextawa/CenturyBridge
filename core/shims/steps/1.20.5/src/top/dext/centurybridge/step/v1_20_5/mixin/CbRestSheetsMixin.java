package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestBannerRenderClient;

/**
 * A material a mod put into 1.20.4's Sheets.BANNER_MATERIALS / SHIELD_MATERIALS under a pattern key is
 * what 1.20.4's renderers drew that pattern with; 1.20.5's Sheets answers it for the pattern of that key
 * (CbRestBannerRenderClient keeps the maps). Without such a put, 1.20.5's own material.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4722.class)
public abstract class CbRestSheetsMixin {

    @Inject(method = "method_33081", at = @At("HEAD"), cancellable = true)
    private static void cbRest$bannerMaterial(net.minecraft.class_6880<net.minecraft.class_2582> pattern,
                                              CallbackInfoReturnable<net.minecraft.class_4730> cir) {
        net.minecraft.class_4730 m = CbRestBannerRenderClient.putFor(pattern, true);
        if (m != null) {
            cir.setReturnValue(m);
        }
    }

    @Inject(method = "method_33083", at = @At("HEAD"), cancellable = true)
    private static void cbRest$shieldMaterial(net.minecraft.class_6880<net.minecraft.class_2582> pattern,
                                              CallbackInfoReturnable<net.minecraft.class_4730> cir) {
        net.minecraft.class_4730 m = CbRestBannerRenderClient.putFor(pattern, false);
        if (m != null) {
            cir.setReturnValue(m);
        }
    }
}
