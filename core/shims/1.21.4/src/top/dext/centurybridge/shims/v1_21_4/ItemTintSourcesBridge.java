package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_10402;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_21_4.CbLegacyItemModels;

/** Registers centurybridge:legacy_color beside vanilla's tint sources (see CbLegacyItemModels). */
@Mixin(class_10402.class)
public abstract class ItemTintSourcesBridge {

    @Inject(method = "method_65394", at = @At("TAIL"))
    private static void cb$legacyColor(CallbackInfo ci) {
        class_10402.field_55235.method_65325(CbLegacyItemModels.LEGACY_COLOR, CbLegacyItemModels.LegacyColor.CODEC);
    }
}
