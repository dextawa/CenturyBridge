package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_437;
import net.minecraft.class_4388;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;

/**
 * The injector into 1.21.6's own RealmsConfigureWorldScreen.init that goes with {@link CbRealmsConfigureWorldRestoredMixin}
 * (its given-back fields stay there, at {@code CbVanillaReads.PRIORITY}): 1.21.5's leftX / rightX from the screen
 * width, set at init's head as 1.21.5's init set them first (init runs again on every resize, in both releases).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(class_4388.class)
public abstract class CbRealmsConfigureWorldRestoredMixinHooks {

    @Inject(method = "method_25426()V", at = @At("HEAD"))
    private void cb$layout(CallbackInfo ci) {
        ((CbPrioritySplit.RealmsConfigureWorldLayout) (Object) this).cb$v1_21_6$layout(((class_437) (Object) this).field_22789);
    }
}
