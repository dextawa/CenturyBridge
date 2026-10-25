package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;
import top.dext.centurybridge.rt.v1_20_2.CbGuiSkins;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiUserBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_320.class)
public abstract class CbGuiUserBridgeHooks {

    private static final CbGuiOverrides.Hook CB_GET_UUID = new CbGuiOverrides.Hook(net.minecraft.class_320.class, "method_1673");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keepUuidString(String name, java.util.UUID uuid, String accessToken, java.util.Optional<String> xuid,
                                   java.util.Optional<String> clientId, net.minecraft.class_320.class_321 type, CallbackInfo ci) {
        ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.User) (Object) this).cb$setUuidString(CbGuiSkins.cbTakeUuid());
    }

    @Inject(method = "method_44717", at = @At("HEAD"), cancellable = true)
    private void cb$profileIdFromUuid(CallbackInfoReturnable<java.util.UUID> cir) {
        if (CB_GET_UUID.in(this)) {
            cir.setReturnValue(CbGuiSkins.cbParseUuid(((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.User) (Object) this).method_1673()));
        }
    }
}
