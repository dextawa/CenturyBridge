package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/** 1.21.11's LiquidBlockRendererMixin.onResourceReloadReturn: 1.21.11 fluid handlers reload with the models */
@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class CbFapiModelManagerFluidMixin {

    @Inject(method = "apply", at = @At("TAIL"))
    private void cb$reloadLegacyFluidHandlers(CallbackInfo ci) {
        CbFapiFluids.onModelsApplied(((ModelManager) (Object) this).getFluidStateModelSet());
    }
}
