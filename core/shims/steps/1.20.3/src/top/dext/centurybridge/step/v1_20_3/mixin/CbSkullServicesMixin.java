package top.dext.centurybridge.step.v1_20_3.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbSkullServices;

/**
 * SkullBlockEntity's 1.20.2 profile cache and session service (CbSkullServices) kept as 1.20.2 kept them, and 1.20.3's
 * profile lookup reading them.
 * <p>
 * 1.20.2: setup(services, executor) stored services.profileCache() and services.sessionService(), clear() nulled them;
 * fetchGameProfile(name) looked the name up in the stored cache (none stored: an empty answer) and filled the textures
 * in through the stored session service. 1.20.3 captures the services in its lookup cache's loader at setup and asks
 * them on each lookup (fetchProfileByName -> services.profileCache().getAsync(name), then
 * services.sessionService().fetchProfile(id) in its continuation).
 * <p>
 * The stored values are exactly what the loader holds unless a mod stored another one, so the lookup takes the stored
 * value where 1.20.3 asks the services: unchanged, that is the same object and 1.20.3 runs as it is. A cache or
 * session service a mod stored is the one asked, and a profile cache a mod nulled answers every lookup empty, as on
 * 1.20.2. A session service a mod nulled leaves 1.20.3's own (1.20.2 then left profiles without textures).
 */
@Mixin(net.minecraft.class_2631.class)
public abstract class CbSkullServicesMixin {

    /** 1.20.3: whether the lookup cache is unset (between clear() and setup) */
    @Shadow
    private static boolean method_54083() {
        throw new AssertionError();
    }

    /** setup(services, executor): 1.20.2 stored both services */
    @Inject(method = "method_39765", at = @At("TAIL"))
    private static void cb$storeServices(net.minecraft.class_7497 services, java.util.concurrent.Executor executor, CallbackInfo ci) {
        CbSkullServices.field_12089(services.comp_840());
        CbSkullServices.field_12088(services.comp_837());
    }

    /** clear(): 1.20.2 nulled both */
    @Inject(method = "method_39766", at = @At("TAIL"))
    private static void cb$clearServices(CallbackInfo ci) {
        CbSkullServices.field_12089((net.minecraft.class_3312) null);
        CbSkullServices.field_12088((MinecraftSessionService) null);
    }

    /** fetchGameProfile(name): with the profile cache nulled while 1.20.3 is set up, 1.20.2 answered empty */
    @Inject(method = "method_52580", at = @At("HEAD"), cancellable = true)
    private static void cb$cacheNulled(String name, CallbackInfoReturnable<CompletableFuture<Optional<GameProfile>>> cir) {
        if (CbSkullServices.field_12089() == null && !method_54083()) {
            cir.setReturnValue(CompletableFuture.completedFuture(Optional.empty()));
        }
    }

    /** fetchProfileByName: the profile cache 1.20.2 asked is the stored one */
    @ModifyExpressionValue(method = "method_54081",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7497;comp_840()Lnet/minecraft/class_3312;"))
    private static net.minecraft.class_3312 cb$storedCache(net.minecraft.class_3312 original) {
        net.minecraft.class_3312 stored = CbSkullServices.field_12089();
        return stored != null ? stored : original;
    }

    /** the textures continuation: the session service 1.20.2 asked is the stored one */
    @ModifyExpressionValue(method = "method_54082",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7497;comp_837()Lcom/mojang/authlib/minecraft/MinecraftSessionService;"))
    private static MinecraftSessionService cb$storedSession(MinecraftSessionService original) {
        MinecraftSessionService stored = CbSkullServices.field_12088();
        return stored != null ? stored : original;
    }
}
