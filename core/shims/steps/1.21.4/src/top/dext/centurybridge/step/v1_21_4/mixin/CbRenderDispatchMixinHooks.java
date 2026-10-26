package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own BlockEntityRenderDispatcher.onResourceManagerReload that goes with
 * {@link CbRenderDispatchMixin} (its restored 1.21.3 members stay there, at {@code CbVanillaReads.PRIORITY}): a
 * dispatcher built with 1.21.3's Suppliers asks them when it reloads, as 1.21.3 did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_824.class)
public abstract class CbRenderDispatchMixinHooks {

    @Inject(method = "method_14491", at = @At("HEAD"))
    private void cb$askSuppliers(net.minecraft.class_3300 resourceManager, CallbackInfo ci) {
        ((CbPrioritySplitClient.DispatchSuppliers) (Object) this).cb$v1_21_4$askSuppliers();
    }
}
