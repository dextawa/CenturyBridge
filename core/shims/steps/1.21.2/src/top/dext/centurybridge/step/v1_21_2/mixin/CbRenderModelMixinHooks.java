package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelBase;
import top.dext.centurybridge.step.v1_21_2.rt.Guard;

/**
 * The injector into 1.21.2's own Model.renderToBuffer that goes with {@link CbRenderModelMixin} (its restored 1.21.1
 * members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them): a model
 * whose class draws itself runs its 1.21.1 renderToBuffer instead, once; a QuadrupedModel built by 1.21.1's constructor
 * draws as 1.21.1's AgeableListModel did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3879.class)
public abstract class CbRenderModelMixinHooks {

    @Inject(method = "method_62100(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V", at = @At("HEAD"), cancellable = true)
    private void cb$drawItself(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color, CallbackInfo ci) {
        if ((Object) this instanceof top.dext.centurybridge.step.v1_21_2.rt.CbRenderQuadruped.Legacy quadruped
            && quadruped.cb$drawLegacy(pose, consumer, light, overlay, color)) {
            // a QuadrupedModel built by 1.21.1's constructor draws as 1.21.1's AgeableListModel did (CbRenderQuadruped)
            ci.cancel();
            return;
        }
        if (CbRenderModelBase.drawsItself(this) && Guard.cbEnter(this)) {
            try {
                ((CbPrioritySplitClient.ModelDraw) (Object) this).cb$v1_21_2$renderToBuffer(pose, consumer, light, overlay, color);
            } finally {
                Guard.cbExit(this);
            }
            ci.cancel();
        }
    }
}
