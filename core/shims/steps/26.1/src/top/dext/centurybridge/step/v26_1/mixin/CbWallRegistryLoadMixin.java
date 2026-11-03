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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistryLoads;

/**
 * RegistryLoadTask.registerTags HEAD: the registry has its elements and its tags are not yet bound (javap of 26.1:
 * ResourceManagerRegistryLoadTask / NetworkRegistryLoadTask call registerElements, then registerTags, which binds them,
 * in load's last stage; freezeRegistry comes later) -- 26.1's equivalent of 1.21.11's RegistryDataLoader.Loader
 * loadFromResources / loadFromNetwork TAIL, which also ran before the tags were bound. So the 1.21 layer's entries for
 * 1.20.6 mods' objects are in the registry when its bindTags mixin (CbLegacyRegistryTagsMixin) adds them to their tags.
 * Hands the registry to the climbing 1.21 layer (rt/CbWallRegistryLoads); does nothing without one. Default priority,
 * reads only; vanilla's registerTags runs unchanged.
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

    @Inject(method = "registerTags", at = @At("HEAD"))
    private void cb$loaded(Map<?, ?> tags, CallbackInfo ci) {
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
