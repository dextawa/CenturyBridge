package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Where 1.20.1 cleared Minecraft's "connected to Realms" flag (setConnectedToRealms(false) at the start of
 * init): the title screen, the Realms main screen and the Realms disconnect screen. The flag then defers to
 * 1.20.2's own answer again (CbGuiMinecraftBridge).
 */
@Mixin({net.minecraft.class_442.class, net.minecraft.class_4325.class, net.minecraft.class_4899.class})
public abstract class CbGuiRealmsFlagResetBridge {

    @Inject(method = "method_25426", at = @At("HEAD"))
    private void cb$forgetRealmsFlag(CallbackInfo ci) {
        ((top.dext.centurybridge.rt.v1_20_2.CbRealmsFlag) net.minecraft.class_310.method_1551()).cb$forgetRealmsFlag();
    }
}
