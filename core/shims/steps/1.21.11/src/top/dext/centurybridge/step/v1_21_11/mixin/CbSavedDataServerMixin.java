package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbSavedDataTypes;

/** MinecraftServer.runServer / stopServer: the running server whose overworld is 1.21.10's SavedData.Context (rt/CbSavedDataTypes) */
@Mixin(MinecraftServer.class)
public abstract class CbSavedDataServerMixin {
    @Inject(method = "method_29741", at = @At("HEAD"))
    private void cb$savedDataServerRunning(CallbackInfo ci) {
        CbSavedDataTypes.started((MinecraftServer) (Object) this);
    }

    @Inject(method = "method_3782", at = @At("TAIL"))
    private void cb$savedDataServerStopped(CallbackInfo ci) {
        CbSavedDataTypes.stopped((MinecraftServer) (Object) this);
    }
}
