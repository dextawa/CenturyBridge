package cb.mixinrestore.mixin;

import cb.mixinrestore.Driver;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FriendlyByteBuf.writeGameProfile(GameProfile) (class_2540.method_43616): through 1.20.4 it writes the id, then the
 * name with writeUtf (method_10814), then the properties; 1.20.5 dropped it and the 1.20.5 step layer restores it with
 * the same calls, merged into FriendlyByteBuf by a layer mixin. An INVOKE injector (not HEAD/RETURN/TAIL) on the
 * writeUtf call, as 1.20.1-era mods wrote it: Mixin lets it into the merged member only when the layer mixin's
 * priority is below the mod's (InjectionPoint.checkPriority).
 */
@Mixin(net.minecraft.class_2540.class)
public abstract class FriendlyByteBufMixin {
    @Inject(method = "method_43616(Lcom/mojang/authlib/GameProfile;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2540;method_10814(Ljava/lang/String;)Lnet/minecraft/class_2540;"),
        remap = false)
    private void cbrestore$writeName(GameProfile profile, CallbackInfo ci) {
        Driver.ran("buf-writeGameProfile-invoke", "name=" + profile.getName());
    }
}
