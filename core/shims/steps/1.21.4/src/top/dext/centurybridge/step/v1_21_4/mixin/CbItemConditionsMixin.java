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

/** Registers centurybridge:legacy_override beside vanilla's conditional item model properties (see CbItemModels) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10459.class)
public abstract class CbItemConditionsMixin {

    /** ID_MAPPER */
    @Shadow
    @Final
    private static net.minecraft.class_5699.class_10388<net.minecraft.class_2960, com.mojang.serialization.MapCodec<? extends net.minecraft.class_10460>> field_55372;

    @Inject(method = "method_65639", at = @At("TAIL"))
    private static void cb$legacyOverride(CallbackInfo ci) {
        field_55372.method_65325(CbItemModels.LEGACY_OVERRIDE, CbItemModels.LegacyOverrideCondition.CODEC);
    }
}
