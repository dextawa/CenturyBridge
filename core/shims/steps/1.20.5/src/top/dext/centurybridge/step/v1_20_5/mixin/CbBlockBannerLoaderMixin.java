package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/**
 * A world's datapack registries as 1.20.4's banner patterns need them (CbBlockBanner): once the banner
 * pattern registry has its entries -- from datapacks on the server, from the server's data on a client
 * -- the patterns 1.20.4 code registered join it (server) and every entry gets its 1.20.4 facade.
 */
@Mixin(net.minecraft.class_7655.class)
public abstract class CbBlockBannerLoaderMixin {

    @Inject(method = "method_45122", at = @At("TAIL"))
    private static void cb$fromDatapacks(net.minecraft.class_3300 resources, net.minecraft.class_6903.class_7863 lookup,
            net.minecraft.class_2385<?> registry, com.mojang.serialization.Decoder<?> decoder,
            java.util.Map<net.minecraft.class_5321<?>, Exception> errors, CallbackInfo ci) {
        CbBlockBanner.loaded(registry, true);
    }

    @Inject(method = "method_56518", at = @At("TAIL"))
    private static void cb$fromNetwork(java.util.Map<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>,
            java.util.List<net.minecraft.class_7782.class_9176>> entries, net.minecraft.class_5912 resources,
            net.minecraft.class_6903.class_7863 lookup, net.minecraft.class_2385<?> registry, com.mojang.serialization.Decoder<?> decoder,
            java.util.Map<net.minecraft.class_5321<?>, Exception> errors, CallbackInfo ci) {
        CbBlockBanner.loaded(registry, false);
    }
}
