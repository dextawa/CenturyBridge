package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1923;
import net.minecraft.class_9240;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbWorldServer;

/**
 * MinecraftServer.reportChunkLoadFailure / reportChunkSaveFailure with the 1.20.6 "no failure" sentinel
 * (CbWorldServer.NO_FAILURE): 1.20.6's reportChunkLoadFailure(pos) and reportChunkSaveFailure(pos) had empty
 * bodies, so a report a 1.20.6 caller makes -- or a 1.20.6 override's super call outside a 1.21 report --
 * returns here before 1.21's logging and crash-report write. 1.21 always passes the real exception.
 */
@Mixin(MinecraftServer.class)
public abstract class CbWorldServerMixin {

    @Inject(method = "method_57821(Ljava/lang/Throwable;Lnet/minecraft/class_9240;Lnet/minecraft/class_1923;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyLoadReport(Throwable failure, class_9240 storage, class_1923 pos, CallbackInfo ci) {
        if (failure == CbWorldServer.NO_FAILURE) {
            ci.cancel();
        }
    }

    @Inject(method = "method_57822(Ljava/lang/Throwable;Lnet/minecraft/class_9240;Lnet/minecraft/class_1923;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacySaveReport(Throwable failure, class_9240 storage, class_1923 pos, CallbackInfo ci) {
        if (failure == CbWorldServer.NO_FAILURE) {
            ci.cancel();
        }
    }
}
