package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbServers;

/** MinecraftServer.runServer / stopServer: the running server and its stop, for the whole layer (CbServers) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbServersMixin {

    @Inject(method = "method_29741", at = @At("HEAD"))
    private void cbServers$running(CallbackInfo ci) {
        CbServers.started((net.minecraft.server.MinecraftServer) (Object) this);
    }

    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cbServers$stopped(CallbackInfo ci) {
        CbServers.stopped((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
