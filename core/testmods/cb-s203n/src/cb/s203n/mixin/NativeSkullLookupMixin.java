package cb.s203n.mixin;

import cb.s203n.S203Native;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * native 1.20.3: SkullBlockEntity.fetchProfileByName(name, services, cancelled) -- the lookup CenturyBridge's 1.20.3
 * layer routes through a stored 1.20.2 profile cache. Both of these must still act with the route on.
 */
@Mixin(net.minecraft.class_2631.class)
public abstract class NativeSkullLookupMixin {

    @Inject(method = "method_54081", at = @At("HEAD"))
    private static void cbs203n$lookup(String name, net.minecraft.class_7497 services, BooleanSupplier cancelled,
            CallbackInfoReturnable<CompletableFuture<Optional<com.mojang.authlib.GameProfile>>> cir) {
        S203Native.LOOKUPS.add(name);
    }

    @ModifyExpressionValue(method = "method_54081",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7497;comp_840()Lnet/minecraft/class_3312;"))
    private static net.minecraft.class_3312 cbs203n$cache(net.minecraft.class_3312 cache) {
        S203Native.CACHE_READS[0]++;
        return cache;
    }
}
