package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderLayers;

/** 1.21.3's HumanoidModel.setupAnim asked the model for its arm poses (see {@link CbRenderLayers}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_572.class)
public abstract class CbLayerHumanoidModelMixin {

    @Inject(method = "method_17087(Lnet/minecraft/class_10034;)V", at = @At("HEAD"))
    private void cb$legacyArmPoses(net.minecraft.class_10034 state, CallbackInfo ci) {
        CbRenderLayers.legacyArmPoses(this, state);
    }
}
