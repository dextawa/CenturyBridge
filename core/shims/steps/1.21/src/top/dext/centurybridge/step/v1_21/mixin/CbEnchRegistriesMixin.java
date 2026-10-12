package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/**
 * BuiltInRegistries.createContents: once vanilla's registries have their entries and before they
 * freeze, CenturyBridge's enchantment effect component (CbEnchantments.LEGACY) joins 1.21's.
 */
@Mixin(net.minecraft.class_7923.class)
public abstract class CbEnchRegistriesMixin {

    @Inject(method = "method_47487", at = @At("TAIL"))
    private static void cbEnch$components(CallbackInfo ci) {
        CbEnchantments.registerComponents(net.minecraft.class_7923.field_51832);
    }
}
