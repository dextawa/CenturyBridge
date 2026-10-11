package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_10443;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_21_4.CbLegacyItemModels;

/** Registers centurybridge:legacy_overrides beside vanilla's item model types (see CbLegacyItemModels). */
@Mixin(class_10443.class)
public abstract class ItemModelTypesBridge {

    @Inject(method = "method_65599", at = @At("TAIL"))
    private static void cb$legacyOverrides(CallbackInfo ci) {
        class_10443.field_55336.method_65325(CbLegacyItemModels.OVERRIDES, CbLegacyItemModels.Unbaked.CODEC);
    }
}
