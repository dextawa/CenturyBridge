package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbLegacyRegistries;

/**
 * RegistryDataLoader.Loader: when a world registry has loaded its contents -- from the datapacks on a
 * server, from the server's data on a client -- and before it freezes, CbLegacyRegistries records it as
 * the world's, and the stand-in of a builtin registry 1.21 replaced by it adds the entries of 1.20.6 mods'
 * objects (server).
 */
@Mixin(targets = "net.minecraft.class_7655$class_9158")
public abstract class CbLegacyRegistryLoaderMixin {

    @Shadow
    @Final
    net.minecraft.class_7655.class_7657<?> comp_2245;

    @Shadow
    @Final
    net.minecraft.class_2385<?> comp_2246;

    /** loadFromResources(ResourceManager, RegistryInfoLookup) */
    @Inject(method = "method_56520", at = @At("TAIL"))
    private void cbLegacy$loaded(net.minecraft.class_3300 resources, net.minecraft.class_6903.class_7863 lookup, CallbackInfo ci) {
        CbLegacyRegistries.onServerLoaded(comp_2245.comp_985(), comp_2246, lookup);
    }

    /** loadFromNetwork(Map, ResourceProvider, RegistryInfoLookup) */
    @Inject(method = "method_56521", at = @At("TAIL"))
    private void cbLegacy$received(Map<?, List<?>> entries, net.minecraft.class_5912 resources, net.minecraft.class_6903.class_7863 lookup,
            CallbackInfo ci) {
        CbLegacyRegistries.onClientLoaded(comp_2245.comp_985(), comp_2246);
    }
}
