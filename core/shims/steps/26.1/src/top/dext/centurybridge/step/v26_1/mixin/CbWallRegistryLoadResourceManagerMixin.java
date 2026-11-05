package top.dext.centurybridge.step.v26_1.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistryLoads;

/** ResourceManagerRegistryLoadTask.load HEAD: keeps the lookup for the task's freeze (CbWallRegistryLoadMixin); reads only */
@Mixin(ResourceManagerRegistryLoadTask.class)
public abstract class CbWallRegistryLoadResourceManagerMixin {
    @Inject(method = "load", at = @At("HEAD"))
    private void cb$lookup(RegistryOps.RegistryInfoLookup lookup, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        ((CbWallRegistryLoads.LookupHolder) (Object) this).cb$wallLookup(lookup);
    }
}
