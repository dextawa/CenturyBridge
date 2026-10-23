package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbBuiltinItemModels;

/** Registers centurybridge:legacy_builtin beside vanilla's special model renderers (SpecialModelRenderers.bootstrap) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10517.class)
public abstract class CbSpecialModelTypesMixin {

    /** ID_MAPPER */
    @Shadow
    @Final
    private static net.minecraft.class_5699.class_10388<net.minecraft.class_2960, com.mojang.serialization.MapCodec<? extends net.minecraft.class_10515.class_10516>> field_55453;

    @Inject(method = "method_65712", at = @At("TAIL"))
    private static void cb$legacyBuiltin(CallbackInfo ci) {
        field_55453.method_65325(CbBuiltinItemModels.LEGACY_BUILTIN, CbBuiltinItemModels.Unbaked.CODEC);
    }
}
