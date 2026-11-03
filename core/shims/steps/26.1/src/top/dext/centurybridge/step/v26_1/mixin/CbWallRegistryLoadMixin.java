package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.NetworkRegistryLoadTask;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistryLoads;

/**
 * RegistryLoadTask.freezeRegistry HEAD: the registry has its elements and tags (ResourceManagerRegistryLoadTask /
 * NetworkRegistryLoadTask register both in load's last stage, javap of 26.1) and is not frozen -- 26.1's equivalent of
 * 1.21.11's RegistryDataLoader.Loader.loadFromResources / loadFromNetwork TAIL. Hands it to the climbing 1.21 layer
 * (rt/CbWallRegistryLoads); does nothing without one. Default priority, reads only; vanilla's freeze runs unchanged.
 */
@Mixin(RegistryLoadTask.class)
public abstract class CbWallRegistryLoadMixin implements CbWallRegistryLoads.LookupHolder {

    @Shadow
    @Final
    private WritableRegistry<?> registry;

    @Shadow
    protected abstract ResourceKey<? extends Registry<?>> registryKey();

    @Unique
    private RegistryOps.RegistryInfoLookup cb$lookup;

    @Override
    public void cb$wallLookup(RegistryOps.RegistryInfoLookup lookup) {
        this.cb$lookup = lookup;
    }

    @Inject(method = "freezeRegistry", at = @At("HEAD"))
    private void cb$loaded(Map<ResourceKey<?>, Exception> errors, CallbackInfoReturnable<Boolean> cir) {
        CbWallRegistryLoads.Hook hook = CbWallRegistryLoads.hook();
        if (hook == null) {
            return;
        }
        if ((Object) this instanceof NetworkRegistryLoadTask) {
            hook.cb$clientLoaded(this.registryKey(), this.registry);
        } else {
            hook.cb$serverLoaded(this.registryKey(), this.registry, this.cb$lookup);
        }
    }
}
