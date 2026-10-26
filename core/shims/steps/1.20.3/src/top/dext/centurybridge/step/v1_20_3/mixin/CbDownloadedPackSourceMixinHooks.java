package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbPacks;

/**
 * The injector into 1.20.3's own DownloadedPackSource.createRepositorySource that goes with
 * {@link CbDownloadedPackSourceMixin} (its restored 1.20.2 members stay there, at {@code CbVanillaReads.PRIORITY}): the
 * repository source 1.20.3's Minecraft registers offers the 1.20.2 server pack slot too, after 1.20.3's own packs.
 */
@Mixin(net.minecraft.class_1066.class)
public abstract class CbDownloadedPackSourceMixinHooks {

    @Inject(method = "method_55514", at = @At("RETURN"), cancellable = true)
    private void cb$offerSlot(CallbackInfoReturnable<net.minecraft.class_3285> cir) {
        cir.setReturnValue(CbPacks.cbWithSlot((net.minecraft.class_1066) (Object) this, cir.getReturnValue()));
    }
}
