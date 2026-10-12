package top.dext.centurybridge.step.v1_21.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1132;
import net.minecraft.class_1923;
import net.minecraft.class_9240;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbWorldServer;

/**
 * IntegratedServer's 1.21 reportChunkLoadFailure / reportChunkSaveFailure call super and then warn about disk
 * space and show a toast; 1.20.6's IntegratedServer had no such override (MinecraftServer's empty body ran).
 * A report with the 1.20.6 "no failure" sentinel (CbWorldServer.NO_FAILURE) is that empty body here too.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1132.class)
public abstract class CbWorldServerClientMixin {

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
