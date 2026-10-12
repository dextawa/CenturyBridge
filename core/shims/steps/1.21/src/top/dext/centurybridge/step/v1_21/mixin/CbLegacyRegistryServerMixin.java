package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbLegacyRegistries;

/** MinecraftServer.stopServer: the stopped server's world registries are no world's any more (CbLegacyRegistries) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbLegacyRegistryServerMixin {

    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cbLegacy$stopped(CallbackInfo ci) {
        CbLegacyRegistries.onServerStopped();
    }
}
