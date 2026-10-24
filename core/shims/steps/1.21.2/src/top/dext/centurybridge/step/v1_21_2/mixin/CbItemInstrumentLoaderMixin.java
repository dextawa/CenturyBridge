package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemInstruments;

/**
 * RegistryDataLoader.Loader: when the minecraft:instrument world registry has loaded -- from the datapacks on a
 * server, before it freezes; from the server's data on a client -- CbItemInstruments records it, and on the server
 * the instruments 1.21.1 mods registered in BuiltInRegistries.INSTRUMENT (the stand-in) join it.
 */
@Mixin(targets = "net.minecraft.class_7655$class_9158")
public abstract class CbItemInstrumentLoaderMixin {

    @Shadow
    @Final
    net.minecraft.class_7655.class_7657<?> comp_2245;

    @Shadow
    @Final
    net.minecraft.class_2385<?> comp_2246;

    /** loadFromResources(ResourceManager, RegistryInfoLookup) */
    @SuppressWarnings("unchecked")
    @Inject(method = "method_56520", at = @At("TAIL"))
    private void cb$instrumentsLoaded(net.minecraft.class_3300 resources, net.minecraft.class_6903.class_7863 lookup, CallbackInfo ci) {
        if (comp_2245.comp_985() == net.minecraft.class_7924.field_41275) {
            CbItemInstruments.onServerLoaded((net.minecraft.class_2385<net.minecraft.class_7444>) comp_2246);
        }
    }

    /** loadFromNetwork(Map, ResourceProvider, RegistryInfoLookup) */
    @SuppressWarnings("unchecked")
    @Inject(method = "method_56521", at = @At("TAIL"))
    private void cb$instrumentsReceived(Map<?, ?> entries, net.minecraft.class_5912 resources, net.minecraft.class_6903.class_7863 lookup,
            CallbackInfo ci) {
        if (comp_2245.comp_985() == net.minecraft.class_7924.field_41275) {
            CbItemInstruments.onClientLoaded((net.minecraft.class_2378<net.minecraft.class_7444>) comp_2246);
        }
    }
}
