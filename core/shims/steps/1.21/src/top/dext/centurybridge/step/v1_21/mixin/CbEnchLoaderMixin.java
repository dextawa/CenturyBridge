package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/**
 * RegistryDataLoader.Loader: when a world's enchantment registry has loaded its contents -- from the
 * datapacks on a server, from the server's data on a client -- and before it freezes, CbEnchantments
 * adds the records of 1.20.6 mods' enchantments (server) and records the registry as the world's.
 */
@Mixin(targets = "net.minecraft.class_7655$class_9158")
public abstract class CbEnchLoaderMixin {

    @Shadow
    @Final
    net.minecraft.class_7655.class_7657<?> comp_2245;

    @Shadow
    @Final
    net.minecraft.class_2385<?> comp_2246;

    /** loadFromResources(ResourceManager, RegistryInfoLookup) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "method_56520", at = @At("TAIL"))
    private void cbEnch$loaded(net.minecraft.class_3300 resources, net.minecraft.class_6903.class_7863 lookup, CallbackInfo ci) {
        if (net.minecraft.class_7924.field_41265.equals(comp_2245.comp_985())) {
            CbEnchantments.onServerLoaded((net.minecraft.class_2385) comp_2246, lookup);
        }
    }

    /** loadFromNetwork(Map, ResourceProvider, RegistryInfoLookup) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "method_56521", at = @At("TAIL"))
    private void cbEnch$received(Map<?, List<?>> entries, net.minecraft.class_5912 resources, net.minecraft.class_6903.class_7863 lookup,
            CallbackInfo ci) {
        if (net.minecraft.class_7924.field_41265.equals(comp_2245.comp_985())) {
            CbEnchantments.onClientLoaded((net.minecraft.class_2385) comp_2246);
        }
    }
}
