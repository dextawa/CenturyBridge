package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemColors;

/** Registers centurybridge:legacy_color beside vanilla's item tint sources (ItemTintSources.bootstrap; see CbItemColors) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10402.class)
public abstract class CbItemTintSourcesMixin {

    /** ID_MAPPER */
    @Shadow
    @Final
    private static net.minecraft.class_5699.class_10388<net.minecraft.class_2960, com.mojang.serialization.MapCodec<? extends net.minecraft.class_10401>> field_55235;

    @Inject(method = "method_65394", at = @At("TAIL"))
    private static void cb$legacyColor(CallbackInfo ci) {
        field_55235.method_65325(CbItemColors.LEGACY_COLOR, CbItemColors.LegacyColor.CODEC);
    }
}
