package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemResults;

/**
 * MinecraftServer: remembers the thread the server ticks on (its constructor takes it), so that a
 * 1.21.1 result constant can be made for the side that asks (CbItemResults: 1.21.2's Success names the
 * side that swings, 1.21.1's SUCCESS swung on whichever side answered it).
 */
@Mixin(MinecraftServer.class)
public abstract class CbItemServerThreadMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$serverThread(CallbackInfo ci) {
        CbItemResults.serverThread(((MinecraftServer) (Object) this).method_3777());
    }
}
