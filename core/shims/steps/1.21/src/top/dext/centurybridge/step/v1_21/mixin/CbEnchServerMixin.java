package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/** MinecraftServer.stopServer: the stopped server's enchantment registry is no world's any more (CbEnchantments) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbEnchServerMixin {

    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cbEnch$stopped(CallbackInfo ci) {
        CbEnchantments.onServerStopped(null);
    }
}
