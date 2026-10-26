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
 * The injector into 1.21.2's own HumanoidModel.getArmPose that goes with {@link CbRenderHumanoidMixin} (its restored
 * 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * while a 1.21.1 call runs 1.21.2 code on this model it answers the put-back arm poses.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_572.class)
public abstract class CbRenderHumanoidMixinHooks {

    @Inject(method = "method_62081", at = @At("HEAD"), cancellable = true)
    private void cb$legacyArmPose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm,
                                  CallbackInfoReturnable<net.minecraft.class_572.class_573> cir) {
        if (CbRenderModelCalls.legacy(this)) {
            CbRenderModelFields.Humanoid fields = (CbRenderModelFields.Humanoid) (Object) this;
            cir.setReturnValue(arm == net.minecraft.class_1306.field_6183 ? fields.cb$rightArmPose() : fields.cb$leftArmPose());
        }
    }
}
