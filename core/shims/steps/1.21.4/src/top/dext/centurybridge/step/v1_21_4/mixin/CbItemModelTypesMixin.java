package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemModels;

/** Registers centurybridge:legacy_overrides beside vanilla's item model types (ItemModels.bootstrap; see CbItemModels) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10443.class)
public abstract class CbItemModelTypesMixin {

    /** ID_MAPPER */
    @Shadow
    @Final
    private static net.minecraft.class_5699.class_10388<net.minecraft.class_2960, com.mojang.serialization.MapCodec<? extends net.minecraft.class_10439.class_10441>> field_55336;

    @Inject(method = "method_65599", at = @At("TAIL"))
    private static void cb$legacyOverrides(CallbackInfo ci) {
        field_55336.method_65325(CbItemModels.LEGACY_OVERRIDES, CbItemModels.Unbaked.CODEC);
    }
}
