package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.level.levelgen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallFeatures;

/** 1.21.11's removed features registered right after 26.1's own (rt/CbWallFeatures) */
@Mixin(Feature.class)
public abstract class CbWallFeatureRegisterMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$registerLegacyFeatures(CallbackInfo ci) {
        CbWallFeatures.register();
    }
}
