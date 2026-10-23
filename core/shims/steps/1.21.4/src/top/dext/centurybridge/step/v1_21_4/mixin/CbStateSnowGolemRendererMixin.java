package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** 1.21.3 SnowGolemRenderer.extractRenderState: the head item is the golem's pumpkin (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_948.class)
public abstract class CbStateSnowGolemRendererMixin {

    @Inject(method = "method_62536", at = @At("TAIL"))
    private void cb$legacyHead(net.minecraft.class_1473 golem, net.minecraft.class_10429 state, float partialTick, CallbackInfo ci) {
        CbRenderStates.extractSnowGolem(golem, state);
    }
}
