package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's container listeners (CbWallSimpleContainerMixin) are told at the end of 26.1's setChanged, as 1.21.11's
 * setChanged told them. Default priority: injector into a 26.1 method.
 */
@Mixin(SimpleContainer.class)
public abstract class CbWallSimpleContainerReadsMixin {

    @Inject(method = "setChanged", at = @At("TAIL"))
    private void cb$changed(CallbackInfo ci) {
        ((CbWallLegacyCalls.ContainerListenersLegacy) (Object) this).cb$notifyListeners();
    }
}
