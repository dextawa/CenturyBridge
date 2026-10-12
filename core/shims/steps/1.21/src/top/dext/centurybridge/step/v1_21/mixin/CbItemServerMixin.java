package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbItemServers;

/** MinecraftServer.runServer / stopServer: the running server, for the 1.20.6 calls that named no level (CbItemServers) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbItemServerMixin {

    @Inject(method = "method_29741", at = @At("HEAD"))
    private void cbItem$running(CallbackInfo ci) {
        CbItemServers.started((net.minecraft.server.MinecraftServer) (Object) this);
    }

    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cbItem$stopped(CallbackInfo ci) {
        CbItemServers.stopped((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
