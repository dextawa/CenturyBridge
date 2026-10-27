package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_243;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;

/**
 * The injector into 1.21.6's own SectionRenderDispatcher.setCameraPosition that goes with
 * {@link CbSectionDispatcherRestoredMixin} (its restored getCameraPosition stays there, at {@code CbVanillaReads.PRIORITY}):
 * the position the setter was given, kept for the restored getter at the setter's return.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(class_846.class)
public abstract class CbSectionDispatcherRestoredMixinHooks {

    @Inject(method = "method_19419(Lnet/minecraft/class_243;)V", at = @At("RETURN"))
    private void cb$recordCameraPosition(class_243 position, CallbackInfo ci) {
        ((CbPrioritySplit.SectionCameraPosition) (Object) this).cb$v1_21_6$recordCameraPosition(position);
    }
}
