package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * PlayerModel's own 1.21.2 getArmPose (the target of javac's bridge from HumanoidModel.getArmPose): while a 1.21.1 call
 * runs 1.21.2 code on the model (CbRenderModelCalls.legacy) it answers the put-back arm poses, as 1.21.1's
 * HumanoidModel.setupAnim read rightArmPose / leftArmPose (see CbRenderHumanoidMixin).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_591.class)
public abstract class CbRenderHumanoidArm591Mixin {

    @Inject(method = "method_62111", at = @At("HEAD"), cancellable = true)
    private void cb$legacyArmPose(net.minecraft.class_10055 state, net.minecraft.class_1306 arm,
                                  CallbackInfoReturnable<net.minecraft.class_572.class_573> cir) {
        if (CbRenderModelCalls.legacy(this)) {
            CbRenderModelFields.Humanoid h = (CbRenderModelFields.Humanoid) this;
            cir.setReturnValue(arm == net.minecraft.class_1306.field_6183 ? h.cb$rightArmPose() : h.cb$leftArmPose());
        }
    }
}
