package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRegistries;

/** Tells CbRegistries which server is running: its registries answer 1.20.4 code off the client thread. */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbRegistriesServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$started(CallbackInfo ci) {
        CbRegistries.serverStarted((net.minecraft.server.MinecraftServer) (Object) this);
    }

    /** stopServer() */
    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cb$stopped(CallbackInfo ci) {
        CbRegistries.serverStopped((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
