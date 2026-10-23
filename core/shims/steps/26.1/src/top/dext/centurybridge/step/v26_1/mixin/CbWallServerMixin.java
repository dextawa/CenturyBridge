package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelData;

/**
 * The running server, for 1.21.11's server level data members that 26.1 moved onto the server
 * (CbWallLevelData): recorded when a server is constructed, cleared when it stops.
 */
@Mixin(MinecraftServer.class)
public abstract class CbWallServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$track(CallbackInfo ci) {
        CbWallLevelData.SERVER = (MinecraftServer) (Object) this;
    }

    @Inject(method = "stopServer", at = @At("TAIL"))
    private void cb$untrack(CallbackInfo ci) {
        if (CbWallLevelData.SERVER == (Object) this) {
            CbWallLevelData.SERVER = null;
        }
    }
}
