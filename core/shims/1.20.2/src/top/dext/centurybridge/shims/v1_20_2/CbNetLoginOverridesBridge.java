package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_3248;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyLogin;
import top.dext.centurybridge.rt.v1_20_2.Guard;

/**
 * ServerLoginNetworkHandler: a 1.20.1 subclass's acceptPlayer and toOfflineProfile overrides are called
 * where vanilla 1.20.2 takes the step those methods stood for.
 *
 * acceptPlayer(): 1.20.1's tick() called it once the login was ready to accept (READY_TO_ACCEPT);
 * 1.20.2's tick() calls verifyLoginAndFinishConnectionSetup(authenticatedProfile) in the same state
 * (VERIFYING), and converted calls of acceptPlayer run that step (CbNet.method_14384). The override runs
 * in its place; its own super.acceptPlayer() reaches vanilla's step again, which the guard lets run.
 *
 * toOfflineProfile(profile): 1.20.1 made a login without a session profile offline with it, from the
 * profile handleHello built (no id, the requested name); 1.20.2's handleHello makes it directly with
 * createOfflineProfile(requestedUsername). The override answers in its place, given the profile 1.20.1
 * passed; its super call is that static (CbNet.method_14375). 1.20.2's authlib refuses a profile without
 * an id; "no id" is Util.NIL_UUID there, as 1.20.2's own NbtUtils.readGameProfile writes where 1.20.1's
 * read a null id.
 */
@Mixin(class_3248.class)
public abstract class CbNetLoginOverridesBridge {

    @Inject(method = "method_52419", at = @At("HEAD"), cancellable = true)
    private void cb$legacyAccept(GameProfile profile, CallbackInfo ci) {
        if (this instanceof CbNetLegacyLogin.Accept legacy && Guard.cbEnter(this)) {
            try {
                legacy.method_14384();
            } finally {
                Guard.cbExit(this);
            }
            ci.cancel();
        }
    }

    @Redirect(method = "method_12641", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3248;method_52418(Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;"))
    private GameProfile cb$legacyOfflineProfile(String name) {
        if (this instanceof CbNetLegacyLogin.OfflineProfile legacy) {
            return legacy.method_14375(new GameProfile(net.minecraft.class_156.field_25140, name));
        }
        return CbNetLoginAccessor.cb$createOfflineProfile(name);
    }
}
