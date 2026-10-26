package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own DisplayInfo.serializeToNetwork(RegistryFriendlyByteBuf) that goes with
 * {@link CbFinDisplayInfoMixin} (its restored 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}): when the
 * display's class overrides 1.20.4's serializeToNetwork, 1.20.5's write is that override.
 */
@Mixin(net.minecraft.class_185.class)
public abstract class CbFinDisplayInfoMixinHooks {

    @Inject(method = "method_813(Lnet/minecraft/class_9129;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyWrite(net.minecraft.class_9129 buf, CallbackInfo ci) {
        if (!CbFinPredicates.cbInDisplayWrite() && CbFinPredicates.cbOverridesDisplayWrite(this.getClass())) {
            boolean outer = CbFinPredicates.cbEnterDisplayWrite();
            try {
                ((CbPrioritySplit.DisplayWrite) (Object) this).cb$v1_20_5$serializeToNetwork((net.minecraft.class_2540) buf);
            } finally {
                CbFinPredicates.cbExitDisplayWrite(outer);
            }
            ci.cancel();
        }
    }
}
