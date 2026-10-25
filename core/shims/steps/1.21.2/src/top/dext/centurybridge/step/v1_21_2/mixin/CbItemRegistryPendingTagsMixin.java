package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRegistries;

/**
 * MappedRegistry's PendingTags (class_2370$3).apply(): the bind goes through the registry's restored bindTags(map),
 * where 1.21.1's ReloadableServerResources.updateRegistryTags and the client's TagCollector called
 * registry.bindTags(load result). With nothing mixed into bindTags the same PendingTags is applied by 1.21.2's own
 * body (CbItemRegistries.bindTags / applyRuns).
 */
@Mixin(targets = "net.minecraft.class_2370$3")
public abstract class CbItemRegistryPendingTagsMixin {

    @Inject(method = "method_62696()V", at = @At("HEAD"), cancellable = true)
    private void cb$bindThroughBindTags(CallbackInfo ci) {
        if (!CbItemRegistries.applyRuns(this)) {
            ci.cancel();
        }
    }
}
