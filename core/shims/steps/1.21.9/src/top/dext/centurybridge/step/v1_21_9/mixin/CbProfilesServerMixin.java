package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbProfiles;

/** Records the running server for CbProfiles (a 1.21.8 override handed back the online player's own profile). */
@Mixin(MinecraftServer.class)
public abstract class CbProfilesServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$recordServer(CallbackInfo ci) {
        CbProfiles.cbServer((MinecraftServer) (Object) this);
    }
}
